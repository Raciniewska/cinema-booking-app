package util

import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import scala.io.StdIn.readLine

object ChooseDateTimeHelper {
  private def isValidDate(startDateTimeString: String): Boolean = {
    try {
      LocalDateTime.parse(startDateTimeString)
    } catch {
      case _: DateTimeParseException => return false
    }
    true
  }

  private def isNotToLate(startDateTimeString: String): Boolean = {
    val d = LocalDateTime.parse(startDateTimeString)
    if (d.isAfter(LocalDateTime.now())) {
      return true
    }
    false
  }

  def selectTimeRange(): Map[String, String] = {
    println("Choose time range you want to see the movie.")
    println("Use date-time format as below:")
    println("YYYY-MM-ddTHH:mm:ss")
    println("Type starting date-time")

    var startDateTimeString = ""
    var valid: Boolean      = false
    while (!valid) {
      startDateTimeString = readLine()
      println(startDateTimeString)
      if (isValidDate(startDateTimeString)) {
        if (isNotToLate(startDateTimeString)) {
          valid = true
        } else {
          println("Cannot make reservation in past")
        }
      } else {
        println("use valid date-time format")
      }
    }

    println("Type ending date-time")

    var endDateTimeString = ""
    valid = false
    while (!valid) {
      endDateTimeString = readLine()
      println(endDateTimeString)
      if (isValidDate(endDateTimeString)) {
        if (LocalDateTime
              .parse(startDateTimeString)
              .isBefore(LocalDateTime.parse(endDateTimeString))) {
          valid = true
        } else {
          println("end date must be after start date")
        }
      } else {
        println("use valid date-time format")
      }
    }
    Map(
      "startDate" -> startDateTimeString,
      "endDate"   -> endDateTimeString
    )
  }
}
