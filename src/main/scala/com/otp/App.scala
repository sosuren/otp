package com.otp

import akka.actor.{Props, ActorSystem}
import com.otp.actors.RequestDistributor
import com.typesafe.config.ConfigFactory

object App {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load("application.conf")

    implicit val system = ActorSystem("otp-system", config)

    val requestDistributor = system.actorOf(Props(classOf[RequestDistributor], config), "request-distributor")

    requestDistributor ! "hello"
  }
}
