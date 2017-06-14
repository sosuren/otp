package com.otp.actors

import akka.actor.{ActorLogging, Actor}
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.graph.Graph
import org.opentripplanner.routing.impl.GraphPathFinder
import org.opentripplanner.standalone.Router

import collection.JavaConverters._

/**
 * Worker to calculate path for routing request
 * @param routerIdToGraphMap [[ Map[String, Graph] ]] Map having router id mapped to Graph
 */
class RequestWorker (routerIdToGraphMap: Map[String, Graph]) extends Actor with ActorLogging {

  val routers = for ((k,g) <- routerIdToGraphMap) yield new Router(k, g)

  def receive = {
    case routingReq: RoutingRequest =>
      log.info(s"Worker received routing request: $routingReq")

      // find paths in every router and pretty print
      routers foreach { router =>
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
}
