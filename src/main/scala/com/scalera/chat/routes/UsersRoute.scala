package com.scalera.chat.routes

import spray.httpx.SprayJsonSupport._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.util.Try

import com.scalera.chat.model.{ Models, ModelJsonFormats, EntityUtils }
import Models._
import ModelJsonFormats._
import EntityUtils._

import com.scalera.chat.database.DatabaseActor._

trait UsersRoute { self: ChatRoute =>

  lazy val usersRoute =
    pathPrefix("users") {
      pathPrefix(Segment){ userId =>
        pathEndOrSingleSlash {
          getUser(userId)
        } ~
        path("messages") {
          getMessagesByUser(userId)
        } ~
        path("rooms") {
          getRoomsByUser(userId)
        }
      } ~
      pathEndOrSingleSlash {
        (post & entity(as[NewUser])) { newUser =>
          createUser(newUser)
        }
      }
    }

  def getUser(userId: Id) = get {
    complete {
      (databaseActor ? GetUser(userId)).mapTo[Option[User]]
    }
  }

  def createUser(newUser: NewUser) = post {
    complete {
      (databaseActor ? CreateUser(newUser)).mapTo[Try[User]]
    }
  }

  def getMessagesByUser(userId: Id) = get {
    complete {
      (databaseActor ? GetMessagesByUser(userId)).mapTo[Try[List[Message]]]
    }
  }

  def getRoomsByUser(userId: Id) = get {
    complete {
      (databaseActor ? GetRoomsByUser(userId)).mapTo[Try[List[Room]]]
    }
  }
}
