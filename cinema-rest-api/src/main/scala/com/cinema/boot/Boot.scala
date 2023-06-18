package com.cinema.boot

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import com.cinema.config.Configuration
import com.cinema.rest.RestServiceActor
import spray.can.Http

object Boot extends App with Configuration {

  implicit val system = ActorSystem("rest-service-example")

  val restService = system.actorOf(Props[RestServiceActor], "rest-endpoint")

  IO(Http) ! Http.Bind(restService, serviceHost, servicePort)
}
