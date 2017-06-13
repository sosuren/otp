package com.otp.actors

import java.io.File

import akka.actor.{Props, ActorLogging, Actor}
import akka.routing.FromConfig
import com.otp.GraphConfigPaths
import com.typesafe.config.Config
import org.opentripplanner.routing.impl.InputStreamGraphSource
import org.opentripplanner.routing.services.GraphService

import collection.JavaConversions._

class RequestDistributor (config: Config) extends Actor with ActorLogging {

  val graphService = new GraphService(config.getBoolean(GraphConfigPaths.AutoReloadGraphService))
  graphService.setDefaultRouterId(config.getString(GraphConfigPaths.DefaultRouterId))

  config.getStringList(GraphConfigPaths.RouterIds).foreach { routerId =>
    graphService.registerGraph(
      routerId,
      new InputStreamGraphSource
        .FileFactory(new File("routers/" + routerId + "/" + InputStreamGraphSource.GRAPH_FILENAME))
        .createGraphSource(routerId)
    )
  }

  val workers = context.actorOf(FromConfig.props(Props(classOf[RequestWorker], graphService)), "request-worker")

  def receive = {
    case x =>
      log.info(s"Distributor received message: $x")
      workers.forward(x)
  }
}
