package com.scalera.chat.routes

import spray.routing._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.Try

trait ChatRoute extends HttpService
  with UsersRoute
  with RoomsRoute
  with MessagesRoute {

  val databaseActor: ActorRef

  implicit def executionContext = actorRefFactory.dispatcher
  implicit val timeout = Timeout(5 seconds)

  lazy val chatRoute =
    usersRoute ~ roomsRoute ~ messagesRoute

}
