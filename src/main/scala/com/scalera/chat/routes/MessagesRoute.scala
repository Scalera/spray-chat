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

trait MessagesRoute { self: ChatRoute =>
      
  lazy val messagesRoute =
    pathPrefix("messages" / Segment) { messageId =>
      pathEndOrSingleSlash {
        getMessage(messageId) ~
        (put & entity(as[NewMessage])) { newMessage =>
          editMessage(messageId, newMessage)
        } ~
        removeMessage(messageId)
      }
    }

  def getMessage(messageId: Id) = get {
    complete {
      (databaseActor ? GetMessage(messageId)).mapTo[Option[Message]]
    }
  }

  def editMessage(messageId: Id, newMessage: NewMessage) = put {
    complete {
      (databaseActor ? EditMessage(messageId, newMessage)).mapTo[Try[Message]]
    }
  }

  def removeMessage(messageId: Id) = delete {
    complete {
      (databaseActor ? RemoveMessage(messageId)).mapTo[Try[Message]]
    }
  }

}
