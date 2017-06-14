package com.otp

import akka.actor.{Props, ActorSystem}
import com.otp.actors.RequestDistributor
import com.typesafe.config.ConfigFactory
import org.opentripplanner.common.model.GenericLocation
import org.opentripplanner.routing.core.RoutingRequest

object App {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()
    implicit val system = ActorSystem("otp-system", config)

    val requestDistributor = system.actorOf(Props(classOf[RequestDistributor], config), "request-distributor")

    getDummyRequests(4) foreach { routeRequest =>

      requestDistributor ! routeRequest
    }
  }

  def getDummyRequests(count: Int): Seq[RoutingRequest] = (1 to count) map { _ =>
    val routingReq = new RoutingRequest()
    routingReq.from = new GenericLocation(45.49768, -122.75986)
    routingReq.to = new GenericLocation(45.51790, -122.65686)
    routingReq
  }
}
