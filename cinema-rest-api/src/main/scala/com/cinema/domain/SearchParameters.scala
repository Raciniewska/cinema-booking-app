package com.cinema.domain

import java.sql.Timestamp
import java.time.LocalDateTime

case class MovieSearchParameters(title: Option[String] = None)

case class ScreeningTimeSearchParameters(startDate: String, endDate: String) {
  def getStartTimestamp: Timestamp = {
    Timestamp.valueOf(LocalDateTime.parse(startDate))
  }

  def getEndTimestamp: Timestamp = {
    Timestamp.valueOf(LocalDateTime.parse(endDate))
  }
}
