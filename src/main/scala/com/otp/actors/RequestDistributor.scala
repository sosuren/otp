package com.otp.actors

import java.io.File

import akka.actor.{Props, ActorLogging, Actor}
import akka.routing.FromConfig
import com.otp.GraphConfigPaths
import com.typesafe.config.Config
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.graph.Graph
import org.opentripplanner.routing.impl.InputStreamGraphSource
import org.opentripplanner.routing.services.GraphService

import collection.JavaConversions._

/**
 * Distributes routing requests to workers pool
 * @param config [[com.typesafe.config.Config]] Configuration for Graph Service
 */
class RequestDistributor (config: Config) extends Actor with ActorLogging {

  // initialize graph service. read auto reload value from config
  val graphService = new GraphService(config.getBoolean(GraphConfigPaths.AutoReloadGraphService))
  // set default router id
  graphService.setDefaultRouterId(config.getString(GraphConfigPaths.DefaultRouterId))

  // initialize graph source factory reading graphs base path from config
  val factory = new InputStreamGraphSource.FileFactory(new File(config.getString(GraphConfigPaths.BasePath)))

  // for every router ids read from config, try to load and register graph
  // on failure throw exception and then exit
  val routerIdToGraphMap: Map[String, Graph] = config.getStringList(GraphConfigPaths.RouterIds).map { routerId =>
    if(!graphService.registerGraph(
      routerId,
      factory.createGraphSource(routerId)
    )) {
      throw new RuntimeException(s"Failed to register graph with router id: $routerId")
      System.exit(1)
    }
    routerId -> graphService.getRouter(routerId).graph
  }.toMap

  // create workers. configurations are loaded from path "akka.actor.deployment" section for path /request-distributor/request-worker
  // scheme chosen from config is ROUND ROBIN POOL
  val workers = context.actorOf(FromConfig.props(Props(classOf[RequestWorker], routerIdToGraphMap)), "request-worker")

  def receive = {
    case routingReq: RoutingRequest =>
      log.info(s"Distributor received routing request: $routingReq")
      workers.forward(routingReq)
  }
}
