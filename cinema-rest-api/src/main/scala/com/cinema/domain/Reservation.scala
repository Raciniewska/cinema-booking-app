package com.cinema.domain
import scala.slick.driver.BasicQueryTemplate
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{ForeignKeyAction, ForeignKeyQuery, MappedProjection}

case class ReservationToCreate(
    seat_id: Long,
    screening_id: Long,
    reservation_type_id: Long,
    name: String,
    surname: String
)

case class Reservation(
    id: Option[Long],
    screening_id: Long,
    name: String,
    surname: String
)

object Reservations extends Table[Reservation]("reservations") {

  def id: Column[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name: Column[String] = column[String]("name")

  def surname: Column[String] = column[String]("surname")

  def screening_id: Column[Long] = column[Long]("screening_id")

  def screening: ForeignKeyQuery[Screenings.type, Screening] =
    foreignKey("screening_fk", screening_id, Screenings)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def * : MappedProjection[Reservation, (Option[Long], Long, String, String)] =
    id.? ~ screening_id ~ name ~ surname <> (Reservation, Reservation.unapply _)

  val findById: BasicQueryTemplate[Long, Reservation] = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c
}
