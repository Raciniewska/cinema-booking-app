package com.cinema.domain
import scala.slick.driver.BasicQueryTemplate
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.MappedProjection

case class Room(id: Option[Long], number: Int)

object Rooms extends Table[Room]("rooms") {

  def id: Column[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def number: Column[Int] = column[Int]("number")

  def * : MappedProjection[Room, (Option[Long], Int)] =
    id.? ~ number <> (Room, Room.unapply _)

  val findById: BasicQueryTemplate[Long, Room] = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c
}
