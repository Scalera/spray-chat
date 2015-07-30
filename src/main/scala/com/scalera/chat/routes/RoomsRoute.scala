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

trait RoomsRoute { self: ChatRoute =>

  lazy val roomsRoute =
    pathPrefix("rooms") {
      pathPrefix(Segment) { roomId =>
        pathEndOrSingleSlash {
          getRoom(roomId)
        } ~
        path("messages") {
          getMessagesByRoom(roomId)
        } ~
        pathPrefix("users") {
          pathEndOrSingleSlash {
            getUsersByRoom(roomId)
          } ~
          pathPrefix(Segment) { userId =>
            pathEndOrSingleSlash {
              joinToRoom(roomId, userId) ~
              leaveRoom(roomId, userId)
            } ~
            path("messages") {
              getMessagesByRoomAndUser(roomId, userId) ~
              (post & entity(as[NewMessage])) { newMessage =>
                createMessage(roomId, userId, newMessage)
              }
            }
          }
        }
      } ~
      pathEndOrSingleSlash {
        (post & entity(as[NewRoom])) { newRoom =>
          createRoom(newRoom)
        }
      }
    }

  def getRoom(roomId: Id) = get {
    complete {
      (databaseActor ? GetRoom(roomId)).mapTo[Option[Room]]
    }
  }

  def getMessagesByRoom(roomId: Id) = get {
    complete {
      (databaseActor ? GetMessagesByRoom(roomId)).mapTo[Try[List[Message]]]
    }
  }

  def getUsersByRoom(roomId: Id) = get {
    complete {
      (databaseActor ? GetUsersByRoom(roomId)).mapTo[Try[List[User]]]
    }
  }

  def getMessagesByRoomAndUser(roomId: Id, userId: Id) = get {
    complete {
      (databaseActor ? GetMessagesByRoomAndUser(roomId, userId)).mapTo[Try[List[Message]]]
    }
  }

  def createRoom(newRoom: NewRoom) = post {
    complete {
      (databaseActor ? CreateRoom(newRoom)).mapTo[Try[Room]]
    }
  }

  def joinToRoom(roomId: Id, userName: Id) = post {
    complete {
      (databaseActor ? JoinToRoom(roomId, userName)).mapTo[Try[Room]]
    }
  }

  def leaveRoom(roomId: Id, userName: Id) = delete {
    complete {
      (databaseActor ? LeaveRoom(roomId, userName)).mapTo[Try[Room]]
    }
  }

  def createMessage(roomId: Id, userName: Id, newMessage: NewMessage) = post {
    complete {
      (databaseActor ? CreateMessage(roomId, userName, newMessage)).mapTo[Try[Message]]
    }
  }

}
