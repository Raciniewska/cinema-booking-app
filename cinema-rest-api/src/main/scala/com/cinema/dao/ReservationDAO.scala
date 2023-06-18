package com.cinema.dao

import com.cinema.config.Configuration
import com.cinema.domain.{SeatReserved, _}

import java.sql._
import java.time.LocalDateTime
import scala.math.abs
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.session.Database.threadLocalSession
import scala.util.Random

class ReservationDAO extends Configuration {

  private val db = Database.forURL(
    url = "jdbc:postgresql://%s:%d/%s".format(dbHost, dbPort, dbName),
    user = dbUser,
    password = dbPassword,
    driver = "org.postgresql.Driver"
  )

  db.withSession {
    initDatabase()
  }

  def getReservationTotalCost(reservationId: Long): Either[Failure, Int] = {
    try {
      db.withSession {
        val q = for {
          sr <- SeatsReserved if sr.reservation_id === reservationId
          rt <- ReservationTypes if rt.id === sr.reservation_type_id
        } yield (sr, rt)
        Right(q.run.toList.map(_._2).map(_.price).sum)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def addAnotherSeatToReservation(
      seatReserved: SeatReserved
  ): Either[Failure, SeatReserved] = {
    try {
      val s = db.withSession {
        val seatReservedId = Some(abs(Random.nextInt.toLong))
        SeatsReserved returning SeatsReserved insert seatReserved.copy(id =
          seatReservedId
        )
      }
      Right(s)
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def create(
      reservationToCreate: ReservationToCreate
  ): Either[Failure, Reservation] = {
    try {
      val reservation = db.withSession {
        val seatReservedId = Some(abs(Random.nextInt.toLong))
        val reservationId  = Some(abs(Random.nextInt.toLong))
        val reservation = Reservation(
          reservationId,
          reservationToCreate.screening_id,
          reservationToCreate.name,
          reservationToCreate.surname
        )
        Reservations insert reservation
        val seatReserved = SeatReserved(
          seatReservedId,
          reservationToCreate.screening_id,
          reservationId.get,
          reservationToCreate.seat_id,
          reservationToCreate.reservation_type_id
        )
        SeatsReserved insert seatReserved
        reservation
      }
      Right(reservation)
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def searchMoviesInTimeRange(
      params: ScreeningTimeSearchParameters
  ): Either[Failure, List[(Movie, Screening)]] = {
    try {
      db.withSession {
        val q = for {
          s <- Screenings
          if s.time >= params.getStartTimestamp && s.time <= params.getEndTimestamp
          m <- Movies if m.id === s.movie_id
        } yield (m, s)

        Right(q.run.toList)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def getScreeningWithAvailableSeatsAndRoom(
      id: Long
  ): Either[Failure, (Screening, Room, List[Seat])] = {
    try {
      db.withSession {
        Screenings.findById(id).firstOption match {
          case Some(screening: Screening) =>
            Rooms.findById(screening.room_id).firstOption match {
              case Some(room: Room) =>
                val reservedSeatsQuery = for {
                  res   <- Reservations if res.screening_id === screening.id
                  s_res <- SeatsReserved if s_res.reservation_id === res.id
                  s     <- Seats if s.room_id === room.id && s.id === s_res.seat_id
                } yield s

                val allSeatsQuery = for {
                  s <- Seats if s.room_id === room.id
                } yield s

                val reservedSeats  = reservedSeatsQuery.run.toList
                val allSeats       = allSeatsQuery.run.toList
                val availableSeats = allSeats diff reservedSeats
                Right(screening, room, availableSeats)
              case _ =>
                Left(notFoundError(s"Not found room id $id"))
            }
          case _ =>
            Left(notFoundError(s"Not found screening id $id"))
        }
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def getReservationTypes: Either[Failure, List[ReservationType]] = {
    try {
      db.withSession {
        val q = for (rt <- ReservationTypes) yield rt
        Right(q.run.toList)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def getReservations: Either[Failure, List[Reservation]] = {
    try {
      db.withSession {
        val q = for (r <- Reservations) yield r
        Right(q.run.toList)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  private def databaseError(e: SQLException): Failure =
    Failure(
      "%d: %s".format(e.getErrorCode, e.getMessage),
      FailureType.DatabaseFailure
    )

  private def notFoundError(message: String): Failure =
    Failure(
      message,
      FailureType.NotFound
    )

  private def initDatabase(): Unit = {
    if (MTable.getTables("movies").list().isEmpty) {
      Movies.ddl.create
      val movies = Seq(
        Movie(Some(1), "Harry Potter"),
        Movie(Some(2), "Król Lew"),
        Movie(Some(3), "Mała Syrenka")
      )
      movies.foreach(el => Movies returning Movies.id insert el)
    }
    if (MTable.getTables("reservation_types").list().isEmpty) {
      ReservationTypes.ddl.create
      val reservationTypes = Seq(
        ReservationType(Some(1), "CHILD", 1250),
        ReservationType(Some(2), "STUDENT", 1800),
        ReservationType(Some(3), "ADULT", 2500)
      )
      reservationTypes.foreach(el =>
        ReservationTypes returning ReservationTypes.id insert el
      )
    }
    if (MTable.getTables("rooms").list().isEmpty) {
      Rooms.ddl.create
      val rooms = Seq(
        Room(Some(1), 1),
        Room(Some(2), 2),
        Room(Some(3), 3)
      )
      rooms.foreach(el => Rooms returning Rooms.id insert el)
    }
    if (MTable.getTables("seats").list().isEmpty) {
      Seats.ddl.create
      val seat_numbers = Seq(1, 2, 3, 4)
      val seat_rows    = Seq("A", "B", "C")
      val rooms        = Seq(1, 2, 3)
      var id           = 0
      for {
        num  <- seat_numbers
        row  <- seat_rows
        room <- rooms
      } yield {
        Seats returning Seats.id insert Seat(Some(id), num, row, room)
        id += 1
      }
    }
    if (MTable.getTables("screenings").list().isEmpty) {
      Screenings.ddl.create
      Screenings returning Screenings.id insert Screening(
        Some(1),
        1,
        1,
        Timestamp.valueOf(LocalDateTime.of(2024, 6, 22, 10, 0, 0))
      )
      Screenings returning Screenings.id insert Screening(
        Some(2),
        2,
        2,
        Timestamp.valueOf(LocalDateTime.of(2024, 6, 22, 15, 0, 0))
      )
      Screenings returning Screenings.id insert Screening(
        Some(3),
        1,
        3,
        Timestamp.valueOf(LocalDateTime.of(2024, 6, 22, 17, 0, 0))
      )
      Screenings returning Screenings.id insert Screening(
        Some(4),
        3,
        1,
        Timestamp.valueOf(LocalDateTime.of(2024, 6, 23, 10, 0, 0))
      )
      Screenings returning Screenings.id insert Screening(
        Some(5),
        2,
        2,
        Timestamp.valueOf(LocalDateTime.of(2024, 6, 23, 13, 0, 0))
      )
      Screenings returning Screenings.id insert Screening(
        Some(6),
        3,
        3,
        Timestamp.valueOf(LocalDateTime.of(2024, 6, 23, 12, 0, 0))
      )
    }
    if (MTable.getTables("reservations").list().isEmpty) {
      Reservations.ddl.create
    }
    if (MTable.getTables("seats_reserved").list().isEmpty) {
      SeatsReserved.ddl.create
    }
  }
}
