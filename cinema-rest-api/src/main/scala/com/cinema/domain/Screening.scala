package com.cinema.domain
import java.sql.Timestamp
import scala.slick.driver.BasicQueryTemplate
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{ForeignKeyAction, ForeignKeyQuery, MappedProjection}

case class Screening(
    id: Option[Long],
    movie_id: Long,
    room_id: Long,
    time: Timestamp
)

object Screenings extends Table[Screening]("screenings") {

  def id: Column[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def room_id: Column[Long] = column[Long]("room_id")

  def room: ForeignKeyQuery[Rooms.type, Room] =
    foreignKey("room_fk", room_id, Rooms)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def movie_id: Column[Long] = column[Long]("movie_id")

  def movie: ForeignKeyQuery[Movies.type, Movie] =
    foreignKey("movie_fk", movie_id, Movies)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  def time: Column[Timestamp] = column[Timestamp]("time")

  def * : MappedProjection[Screening, (Option[Long], Long, Long, Timestamp)] =
    id.? ~ room_id ~ movie_id ~ time <> (Screening, Screening.unapply _)

  val findById: BasicQueryTemplate[Long, Screening] = for {
    id <- Parameters[Long]
    c  <- this if c.id is id
  } yield c
}
