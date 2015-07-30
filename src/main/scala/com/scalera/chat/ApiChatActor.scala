package com.scalera.chat

import akka.actor.{ ActorLogging, Props, ActorRef }
import scala.reflect.runtime.universe._
import spray.routing.HttpServiceActor

import com.gettyimages.spray.swagger._

import routes.{ ChatRoute, UsersRoute, CustomExceptionHandler }

class ApiChatActor(database: ActorRef) extends HttpServiceActor
  with ActorLogging
  with ChatRoute {

  lazy val databaseActor = database

  val apiRoute =
    handleExceptions(CustomExceptionHandler.exceptionHandler) {
      chatRoute
    }

  val swaggerRoute =
    get {
      pathPrefix("swagger") {
        pathEndOrSingleSlash {
          getFromResource("swagger-ui/index.html")
        }
      } ~ getFromResourceDirectory("swagger-ui")
    }

  lazy val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[UsersRoute])
    override def apiVersion = "1.0"
    override def baseUrl = "/"
    override def docsPath = "api-docs"
    override def actorRefFactory = context
    override def apiInfo = None
  }

  def receive = runRoute(apiRoute ~ swaggerService.routes ~ swaggerRoute)
}

object ApiChatActor {
  def props(databaseActor: ActorRef) = Props(new ApiChatActor(databaseActor))
}
