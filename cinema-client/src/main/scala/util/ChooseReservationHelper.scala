package util

import model.ReservationType

import scala.io.StdIn.readLine

object ChooseReservationHelper {

  def getSurname: String = {
    println("please enter your surname")
    var valid        = false
    var surname      = ""
    val surnameRegex = "[A-Z][^0-9 ][^0-9 ][^0-9 ]*"
    while (!valid) {
      surname = readLine()
      println(surname)
      val surnameParts = surname.split("-")
      if (surnameParts.length <= 2) {
        valid = surnameParts.forall(s => s.matches(surnameRegex))
      }
    }
    surname
  }

  def getName: String = {
    println("please enter your name")
    var valid     = false
    var name      = ""
    val nameRegex = "[A-Z][^0-9 ][^0-9 ][^0-9 ]*"
    while (!valid) {
      name = readLine()
      println(name)
      if (name.matches(nameRegex)) {
        valid = true
      }
    }
    name
  }

  private def printReservationTypesInfo(
      reservationTypes: List[ReservationType]
  ): Unit = {
    println("available ticket types:")
    for (r <- reservationTypes) {
      println(s"${r.name} with price: ${r.price.toDouble / 100} ID: ${r.id}")
    }
  }

  def getReservationTypesForSeats(
      reservationTypes: List[ReservationType],
      seats: List[Long]
  ): List[Long] = {
    printReservationTypesInfo(reservationTypes)
    println("please enter ticket type id for each seat:")
    seats.map(s => {
      println(s"enter ticket type for seat $s")
      val choosedType = readLine()
      println(choosedType)
      choosedType.toLong
    })
  }

}
