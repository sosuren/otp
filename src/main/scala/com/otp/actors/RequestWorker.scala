package com.otp.actors

import akka.actor.{ActorLogging, Actor}
import org.opentripplanner.common.model.GenericLocation
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.impl.GraphPathFinder
import org.opentripplanner.routing.services.GraphService

import collection.JavaConverters._

class RequestWorker (gs: GraphService) extends Actor with ActorLogging {

  def receive = {
    case x =>
      log.info(s"Worker received message: $x")
      val routingReq = new RoutingRequest()
      routingReq.from = new GenericLocation(45.49768, -122.75986)
      routingReq.to = new GenericLocation(45.51790, -122.65686)
      routingReq.setRoutingContext(gs.getRouter("pdx").graph)
      val paths =  new GraphPathFinder(gs.getRouter("pdx")).getPaths(routingReq)
      println(s"Paths length: ${paths.size()}")
      paths.asScala.foreach { path =>
        println(s"Walking distance can be: ${path.getWalkDistance}")
      }
  }
}
