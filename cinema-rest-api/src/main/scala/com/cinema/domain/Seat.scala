package com.cinema.domain
import scala.slick.driver.BasicQueryTemplate
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{ForeignKeyAction, ForeignKeyQuery, MappedProjection}

case class Seat(id: Option[Long], number: Int, row: String, room_id: Long)

object Seats extends Table[Seat]("seats") {

  def id: Column[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def number: Column[Int] = column[Int]("number")

  def row: Column[String] = column[String]("row")

  def room_id: Column[Long] = column[Long]("room_id")

  def room: ForeignKeyQuery[Rooms.type, Room] =
    foreignKey("room_fk", room_id, Rooms)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def * : MappedProjection[Seat, (Option[Long], Int, String, Long)] =
    id.? ~ number ~ row ~ room_id <> (Seat, Seat.unapply _)

  val findById: BasicQueryTemplate[Long, Seat] = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c
}
