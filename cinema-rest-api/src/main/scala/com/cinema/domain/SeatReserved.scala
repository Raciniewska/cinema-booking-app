package com.cinema.domain
import scala.slick.driver.BasicQueryTemplate
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{ForeignKeyAction, ForeignKeyQuery, MappedProjection}

case class SeatReserved(
    id: Option[Long],
    screening_id: Long,
    reservation_id: Long,
    seat_id: Long,
    reservation_type: Long
)

object SeatsReserved extends Table[SeatReserved]("seats_reserved") {

  def id: Column[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def seat_id: Column[Long] = column[Long]("seat_id")

  def seat: ForeignKeyQuery[Seats.type, Seat] =
    foreignKey("seat_fk", seat_id, Seats)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def reservation_id: Column[Long] = column[Long]("reservation_id")

  def reservation: ForeignKeyQuery[Reservations.type, Reservation] =
    foreignKey("reservation_fk", reservation_id, Reservations)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def screening_id: Column[Long] = column[Long]("screening_id")

  def screening: ForeignKeyQuery[Screenings.type, Screening] =
    foreignKey("screening_fk", screening_id, Screenings)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def reservation_type_id: Column[Long] = column[Long]("reservation_type_id")

  def reservation_type
      : ForeignKeyQuery[ReservationTypes.type, ReservationType] =
    foreignKey("reservation_type_fk", reservation_type_id, ReservationTypes)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def *
      : MappedProjection[SeatReserved, (Option[Long], Long, Long, Long, Long)] =
    id.? ~ screening_id ~ reservation_id ~ seat_id ~ reservation_type_id <> (SeatReserved, SeatReserved.unapply _)

  val findById: BasicQueryTemplate[Long, SeatReserved] = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c
}
