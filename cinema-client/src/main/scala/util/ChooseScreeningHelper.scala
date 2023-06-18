package util

import model.{MovieAndScreening, Screening}

import scala.io.StdIn.readLine

object ChooseScreeningHelper {

  private def isValidScreeningId(
      screeningId: Long,
      screenings: List[Screening]
  ): Boolean = {
    screenings.find(s => s.id == screeningId) match {
      case Some(_) => true
      case None    => false
    }
  }

  def getScreeningIdFromUser(screenings: List[Screening]): Long = {
    println("Enter screening to make the reservation:")
    var screeningId: Long = 0
    var valid             = false
    val notValidIdMessage = "type correct screening id value"
    while (!valid) {
      try {
        screeningId = readLine().toLong
        println(screeningId)
      } catch {
        case ex: NumberFormatException => println(notValidIdMessage)
      }
      if (isValidScreeningId(screeningId, screenings)) {
        valid = true
      } else {
        println(notValidIdMessage)
      }
    }
    screeningId
  }

  def displayMovieAndScreening(
      moviesAndScreenings: List[MovieAndScreening]
  ): Unit = {
    println("Available screenings at given time range:")
    val sorted = moviesAndScreenings.sortBy(r => (r._1.title, r._2.time))
    sorted.foreach(el =>
      println(
        s"screening id: ${el._2.id} | title: ${el._1.title} | screening time: ${el._2.time}"
      )
    )
  }
}
