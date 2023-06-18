package com.cinema.rest

import akka.actor.{Actor, ActorContext}
import akka.dispatch.MessageDispatcher
import akka.event.slf4j.SLF4JLogging
import com.cinema.dao._
import com.cinema.domain._
import net.liftweb.json.Serialization._
import net.liftweb.json.{DateFormat, Formats}
import spray.http._
import spray.httpx.unmarshalling.Unmarshaller
import spray.routing._

import java.text.SimpleDateFormat
import java.util.Date

class RestServiceActor extends Actor with RestService {

  implicit def actorRefFactory: ActorContext = context

  def receive = runRoute(rest)
}

trait RestService extends HttpService with SLF4JLogging {
  private val reservationService                   = new ReservationDAO
  implicit val executionContext: MessageDispatcher = actorRefFactory.dispatcher

  implicit val liftJsonFormats: Formats = new Formats {
    override val dateFormat: DateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

      def parse(s: String): Option[Date] =
        try {
          Some(sdf.parse(s))
        } catch {
          case e: Exception => None
        }

      def format(d: Date): String = sdf.format(d)
    }
  }

  val rest: Route = respondWithMediaType(MediaTypes.`application/json`) {
    path("screening") {
      get {
        parameters('startDate, 'endDate).as(ScreeningTimeSearchParameters) {
          searchParameters: ScreeningTimeSearchParameters =>
            { ctx: RequestContext =>
              handleRequest(ctx) {
                log.debug(
                  "Searching for screening with parameters: %s".format(
                    searchParameters
                  )
                )
                reservationService.searchMoviesInTimeRange(searchParameters)
              }
            }
        }
      }
    } ~
    path("reservation_another") {
      post {
        entity(Unmarshaller(MediaTypes.`application/json`) {
          case httpEntity: HttpEntity =>
            read[SeatReserved](httpEntity.asString(HttpCharsets.`UTF-8`))
        }) { seatReserved: SeatReserved => ctx: RequestContext =>
          handleRequest(ctx, StatusCodes.Created) {
            log.debug("Adding seats to reservation: %s".format(seatReserved))
            reservationService.addAnotherSeatToReservation(seatReserved)
          }
        }
      }
    } ~
    path("reservation" / LongNumber) { reservationId =>
      get { ctx: RequestContext =>
        handleRequest(ctx) {
          log.debug("Fetching reservation total")
          reservationService.getReservationTotalCost(reservationId)
        }
      }
    } ~
    path("reservation") {
      get { ctx: RequestContext =>
        handleRequest(ctx) {
          log.debug("Fetching reservations")
          reservationService.getReservations
        }
      } ~
      post {
        entity(Unmarshaller(MediaTypes.`application/json`) {
          case httpEntity: HttpEntity =>
            read[ReservationToCreate](httpEntity.asString(HttpCharsets.`UTF-8`))
        }) { reservation: ReservationToCreate => ctx: RequestContext =>
          handleRequest(ctx, StatusCodes.Created) {
            log.debug("Creating reservation: %s".format(reservation))
            reservationService.create(reservation)
          }
        }
      }
    } ~
    path("reservation_types") {
      get { ctx: RequestContext =>
        handleRequest(ctx) {
          log.debug("Fetching reservation types")
          reservationService.getReservationTypes
        }
      }
    } ~
    path("screening" / LongNumber) { screeningId =>
      get { ctx: RequestContext =>
        handleRequest(ctx) {
          log.debug("Retrieving screening with id %d".format(screeningId))
          reservationService.getScreeningWithAvailableSeatsAndRoom(screeningId)
        }
      }
    }
  }

  private def handleRequest(
      ctx: RequestContext,
      successCode: StatusCode = StatusCodes.OK
  )(action: => Either[Failure, _]): Unit = {
    action match {
      case Right(result: Object) =>
        ctx.complete(successCode, write(result))
      case Left(error: Failure) =>
        ctx.complete(
          error.getStatusCode,
          net.liftweb.json.Serialization.write(Map("error" -> error.message))
        )
      case _ =>
        ctx.complete(StatusCodes.InternalServerError)
    }
  }
}
