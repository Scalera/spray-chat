package com.scalera.chat

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import akka.pattern.ask

import spray.can.Http

import scala.concurrent.duration._

import database.InMemoryDatabaseActor

object Main extends App {

  implicit val system = ActorSystem("Chat")

  val databaseActor = system.actorOf(InMemoryDatabaseActor.props)

  val api = system.actorOf(ApiChatActor.props(databaseActor))

  implicit val timeout = Timeout(5 seconds)

  IO(Http) ? Http.Bind(api, interface = "localhost", port = 8080)

}