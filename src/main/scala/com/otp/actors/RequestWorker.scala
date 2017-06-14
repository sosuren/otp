package com.otp.actors

import akka.actor.{ActorLogging, Actor}
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.graph.Graph
import org.opentripplanner.routing.impl.GraphPathFinder
import org.opentripplanner.standalone.Router

import collection.JavaConverters._

class RequestWorker (routerId: String, graph: Graph) extends Actor with ActorLogging {

  val router = new Router(routerId, graph)

  def receive = {
    case routingReq: RoutingRequest =>
      log.info(s"Worker received routing request: $routingReq")
      routingReq.setRoutingContext(router.graph)
      val paths =  new GraphPathFinder(router).getPaths(routingReq)
      val msg =
        s"""
          | ==== **** ====
          | Route From: ${routingReq.from.getCoordinate}
          | Route To: ${routingReq.to.getCoordinate}
          | Details ->
          | Number of paths found: ${paths.size()}
          | Walking Distances are: ${paths.asScala.map{_.getWalkDistance}.mkString("\t")}
          | ==== !!!! ====
        """.stripMargin
      println(msg)
  }
}
