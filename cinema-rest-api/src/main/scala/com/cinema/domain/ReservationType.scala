package com.cinema.domain
import scala.slick.driver.PostgresDriver.simple._

case class ReservationType(id: Option[Long], name: String, price: Int)

object ReservationTypes extends Table[ReservationType]("reservation_types") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def price = column[Int]("price")

  def * = id.? ~ name ~ price <> (ReservationType, ReservationType.unapply _)

  val findById = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c
}
