package com.otp.actors

import akka.actor.{ActorLogging, Actor}
import org.opentripplanner.routing.services.GraphService

class RequestWorker (gs: GraphService) extends Actor with ActorLogging {

  def receive = {
    case x =>
      log.info(s"Worker received message: $x")
  }
}
