package com.otp.actors

import akka.actor.{ActorLogging, Actor}
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.impl.GraphPathFinder
import org.opentripplanner.routing.services.GraphService

import collection.JavaConverters._

class RequestWorker (gs: GraphService) extends Actor with ActorLogging {

  def receive = {
    case routingReq: RoutingRequest =>
      log.info(s"Worker received routing request: $routingReq")
      routingReq.setRoutingContext(gs.getRouter("pdx").graph)
      val paths =  new GraphPathFinder(gs.getRouter("pdx")).getPaths(routingReq)
      println(
        s"""
          | ==== **** ====
          | Route From: ${routingReq.from.getCoordinate}
          | Route To: ${routingReq.to.getCoordinate}
          | Details ->
          | Number of paths found: ${paths.size()}
          | Walking Distances are: ${paths.asScala.map{_.getWalkDistance}.mkString("\t")}
          | ==== !!!! ====
        """.stripMargin
      )
  }
}
