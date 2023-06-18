import io.circe.generic.auto._
import io.circe.parser._
import model.{MovieAndScreening, Reservation, ReservationType, ScreeningRoomAndSeats}
import sttp.client3._
import util.{ChooseDateTimeHelper, ChooseReservationHelper, ChooseScreeningHelper, ChooseSeatsHelper}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CinemaClient {
  def main(args: Array[String]): Unit = {
    val dateRangeParams = ChooseDateTimeHelper.selectTimeRange()
    val backend         = HttpURLConnectionBackend()
    var uri             = uri"http://localhost:8080/screening?$dateRangeParams"
    var response = quickRequest
      .get(uri)
      .send(backend)

    val movieAndScreening = decode[List[MovieAndScreening]](response.body)
    movieAndScreening match {
      case Right(el: List[MovieAndScreening]) =>
        ChooseScreeningHelper.displayMovieAndScreening(el)
        val screeningId =
          ChooseScreeningHelper.getScreeningIdFromUser(el.map(_._2))
        uri = uri"http://localhost:8080/screening/$screeningId"
        response = quickRequest
          .get(uri)
          .send(backend)
        val screeningRoomAndSeats = decode[ScreeningRoomAndSeats](response.body)
        screeningRoomAndSeats match {
          case Right(el: ScreeningRoomAndSeats) =>
            ChooseSeatsHelper.displayRoomAndSeatsInfo(el._2, el._3)
            var seats = ChooseSeatsHelper.getSeatsFromUser(el._3)
            uri = uri"http://localhost:8080/reservation_types"
            response = quickRequest
              .get(uri)
              .send(backend)
            val reservationTypes = decode[List[ReservationType]](response.body)
            reservationTypes match {
              case Right(rt: List[ReservationType]) =>
                var reservationTypes =
                  ChooseReservationHelper.getReservationTypesForSeats(rt, seats)
                val name    = ChooseReservationHelper.getName
                val surname = ChooseReservationHelper.getSurname
                if (LocalDateTime
                      .parse(
                        el._1.time,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                      )
                      .isAfter(LocalDateTime.now().minusMinutes(15))) {
                  uri = uri"http://localhost:8080/reservation"
                  val requestBody =
                    s"""
                       | {"seat_id": ${seats.head},
                       | "screening_id": ${el._1.id},
                       | "reservation_type_id": ${reservationTypes.head},
                       | "name": "$name",
                       | "surname": "$surname"
                       |}""".stripMargin
                  response = quickRequest
                    .post(uri)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .send(backend)
                  val reservation = decode[Reservation](response.body)
                  reservation match {
                    case Right(r: Reservation) =>
                      seats = seats.tail
                      reservationTypes = reservationTypes.tail
                      while (seats.nonEmpty) {
                        uri = uri"http://localhost:8080/reservation_another"
                        val requestBody =
                          s"""
                             | {"screening_id": ${el._1.id},
                             | "reservation_id": ${r.id},
                             | "seat_id": ${seats.head},
                             | "reservation_type": ${reservationTypes.head}
                             |}""".stripMargin
                        response = quickRequest
                          .post(uri)
                          .header("Content-Type", "application/json")
                          .body(requestBody)
                          .send(backend)
                        seats = seats.tail
                        reservationTypes = reservationTypes.tail
                      }
                      uri = uri"http://localhost:8080/reservation/${r.id}"
                      response = quickRequest
                        .get(uri)
                        .send(backend)
                      val cost = decode[Int](response.body)
                      cost match {
                        case Right(c: Int) =>
                          println(
                            s"Total reservation cost = ${c.toDouble / 100}"
                          )
                          println("Reservation completed")
                        case _ =>
                          println("Could not fetch total reservation cost")
                          System.exit(-1)
                      }
                    case _ =>
                      println("Server error")
                      System.exit(-1)
                  }
                } else {
                  println(
                    "reservation can be made up to 15 minutes before screening"
                  )
                  System.exit(-1)
                }

              case _ =>
                println("could not fetch reservation types from server")
                System.exit(-1)
            }

          case _ =>
            println("reservation for this screening is unavailable")
            System.exit(-1)
        }
      case _ =>
        println("no screening for given timerange")
        System.exit(-1)
    }
  }

}
