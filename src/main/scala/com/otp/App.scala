package com.otp

import akka.actor.{Props, ActorSystem}
import com.otp.actors.RequestDistributor
import com.typesafe.config.ConfigFactory
import org.opentripplanner.common.model.GenericLocation
import org.opentripplanner.routing.core.RoutingRequest

object App {

  def main(args: Array[String]): Unit = {

    val port = args(0).toInt
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").withFallback(ConfigFactory.load("application.conf"))

    implicit val system = ActorSystem("otp-system", config)

    val requestDistributor = system.actorOf(Props(classOf[RequestDistributor], config), "request-distributor")

    val dummyRequestsCount = if (port != 2560) 25 else 4
    getDummyRequests(dummyRequestsCount) foreach { routeRequest =>

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
