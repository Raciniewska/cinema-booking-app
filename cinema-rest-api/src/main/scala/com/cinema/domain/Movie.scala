package com.cinema.domain
import scala.slick.driver.PostgresDriver.simple._

case class Movie(id: Option[Long], title: String)

object Movies extends Table[Movie]("movies") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def title = column[String]("title")

  def * = id.? ~ title <> (Movie, Movie.unapply _)

  val findById = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c

}
