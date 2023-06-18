package util

import model.{Room, Seat}

import scala.io.StdIn.readLine

object ChooseSeatsHelper {

  private def seatInList(seats: List[Seat], number: Int): Boolean = {
    seats.find(s => s.number == number) match {
      case Some(_) => true
      case _       => false
    }
  }

  private def validSeatsToReserve(
      seatsToCheck: List[Seat],
      seats: List[Seat]
  ): Boolean = {
    var valid         = true
    val rowsToReserve = seatsToCheck.map(s => s.row).distinct
    rowsToReserve.foreach(rowNo => {
      val seatsToReserveInRow    = seatsToCheck.filter(s => s.row == rowNo)
      val seatsInRow             = seats.filter(s => s.row == rowNo)
      val maxSeatNumberAvailable = seatsInRow.map(s => s.number).max
      var prevReserved           = true
      var prevPrevReserved       = false
      for (n <- 1 to maxSeatNumberAvailable by 1) {
        val tmp = prevReserved
        if (seatInList(seatsInRow, n)) {
          if (seatInList(seatsToReserveInRow, n)) {
            if (prevReserved) {
              prevReserved = true
            } else {
              if (prevPrevReserved) {
                valid = false
                return valid
              }
            }
          } else {
            prevReserved = false
          }
        } else {
          prevReserved = true
        }
        if (n > 1) {
          prevPrevReserved = tmp
        }
      }
    })
    valid
  }

  def getSeatsFromUser(seats: List[Seat]): List[Long] = {
    println(
      "Please enter list of seats ids you want to reserve, use input format as below:"
    )
    println("<seat_id>,<seat_id>,<seat_id>")
    var valid                      = false
    var seatsToReserve: List[Long] = List.empty[Long]
    while (!valid) {
      val inputString = readLine()
      println(inputString)
      val parsedList = inputString.split(",")
      if (parsedList(0) == "") {
        println("seat id cannot be empty")
      } else {
        try {
          val parsedToLong             = parsedList.map(el => el.toLong).toList
          var seatsToCheck: List[Seat] = List.empty
          var foundInvalid             = false
          parsedToLong.foreach(x => {
            seats.find(s => s.id == x) match {
              case Some(s: Seat) => seatsToCheck = seatsToCheck :+ s
              case _ =>
                println(s"Invalid seat id: ${x}")
                foundInvalid = true
            }
          })
          if (!foundInvalid) {
            if (validSeatsToReserve(seatsToCheck, seats)) {
              seatsToReserve = seatsToCheck.map(s => s.id)
              valid = true
            } else {
              println("chosen seats combination is incorrect")
            }
          }
        } catch {
          case _: NumberFormatException =>
            println("please enter ids in valid format")
        }
      }
    }
    seatsToReserve
  }

  def displayRoomAndSeatsInfo(room: Room, seats: List[Seat]): Unit = {
    println(s"Screening is taking place in room number: ${room.number}")
    val rows = seats.map(s => s.row).distinct
    rows.foreach(r => {
      println(s"Seats available in row $r: ")
      val seatsNumberInRow = seats.filter(s => s.row == r)
      seatsNumberInRow.foreach(s =>
        println(s"seat id: ${s.id} | seat number: ${s.number}")
      )
    })
  }

}
