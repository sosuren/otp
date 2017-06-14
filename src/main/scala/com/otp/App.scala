package com.otp

import akka.actor.{Props, ActorSystem}
import com.otp.actors.RequestDistributor
import com.typesafe.config.ConfigFactory
import org.opentripplanner.common.model.GenericLocation
import org.opentripplanner.routing.core.RoutingRequest

object App {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load("application.conf")

    implicit val system = ActorSystem("otp-system", config)

    val requestDistributor = system.actorOf(Props(classOf[RequestDistributor], config), "request-distributor")

    routingRequests foreach { routeRequest =>

      requestDistributor ! routeRequest
    }
  }

  def routingRequests: Seq[RoutingRequest] = (1 to 4) map { _ =>
    val routingReq = new RoutingRequest()
    routingReq.from = new GenericLocation(45.49768, -122.75986)
    routingReq.to = new GenericLocation(45.51790, -122.65686)
    routingReq
  }
}
