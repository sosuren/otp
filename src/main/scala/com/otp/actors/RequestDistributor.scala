package com.otp.actors

import java.io.File

import akka.actor.{Props, ActorLogging, Actor}
import akka.routing.FromConfig
import com.otp.GraphConfigPaths
import com.typesafe.config.Config
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.impl.InputStreamGraphSource
import org.opentripplanner.routing.services.GraphService

import collection.JavaConversions._

class RequestDistributor (config: Config) extends Actor with ActorLogging {

  val graphService = new GraphService(config.getBoolean(GraphConfigPaths.AutoReloadGraphService))
  graphService.setDefaultRouterId(config.getString(GraphConfigPaths.DefaultRouterId))

  val factory = new InputStreamGraphSource.FileFactory(new File(config.getString(GraphConfigPaths.BasePath)))

  config.getStringList(GraphConfigPaths.RouterIds).foreach { routerId =>
    graphService.registerGraph(
      routerId,
      factory.createGraphSource(routerId)
    )
  }

  val workers = context.actorOf(FromConfig.props(Props(classOf[RequestWorker], "pdx", graphService.getRouter("pdx").graph)), "request-worker")

  def receive = {
    case routingReq: RoutingRequest =>
      log.info(s"Distributor received routing request: $routingReq")
      workers.forward(routingReq)
  }
}
