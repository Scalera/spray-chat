package com.scalera.chat

import akka.actor.{ Actor, ActorLogging, Props, ActorRef }

import routes.{ ChatRoute, CustomExceptionHandler }

class ApiChatActor(database: ActorRef) extends Actor with ChatRoute {

  lazy val databaseActor = database

  def actorRefFactory = context

  val apiRoute =
    handleExceptions(CustomExceptionHandler.exceptionHandler) {
      chatRoute
    }

  def receive = runRoute(apiRoute)
}

object ApiChatActor {
  def props(databaseActor: ActorRef) = Props(new ApiChatActor(databaseActor))
}
