package com.scalera.chat.database

import akka.actor.{ Actor, Props }
import scala.util.Try

import com.scalera.chat.model.{ Models, EntityUtils }
import Models._
import EntityUtils._

trait DatabaseActor extends Actor {

  import DatabaseActor._

  def receive = {
    case GetUser(userId) =>
      sender ! getUser(userId)

    case GetRoom(roomId) =>
      sender ! getRoom(roomId)

    case GetMessage(date) =>
      sender ! getMessage(date)

    case GetMessagesByRoom(roomId) =>
      sender ! getMessagesByRoom(roomId)

    case GetMessagesByRoomAndUser(roomId, userId) =>
      sender ! getMessagesByRoomAndUser(roomId, userId)

    case GetMessagesByUser(userId) =>
      sender ! getMessagesByUser(userId)

    case GetRoomsByUser(userId) =>
      sender ! getRoomsByUser(userId)

    case GetUsersByRoom(roomId) =>
      sender ! getUsersByRoom(roomId)

    case CreateUser(newUser) =>
      sender ! createUser(newUser)

    case CreateRoom(newRoom) =>
      sender ! createRoom(newRoom)

    case JoinToRoom(roomId, userId) =>
      sender ! joinToRoom(roomId, userId)

    case LeaveRoom(roomId, userId) =>
      sender ! leaveRoom(roomId, userId)

    case CreateMessage(roomId, userId, newMessage) =>
      sender ! createMessage(roomId, userId, newMessage)

    case RemoveMessage(messageId) =>
      sender ! removeMessage(messageId)

    case EditMessage(messageId, newMessage) =>
      sender ! editMessage(messageId, newMessage)
  }

  def getUser(userId: String): Option[User]

  def getRoom(roomId: String): Option[Room]

  def getMessage(messageId: Id): Option[Message]

  def getMessagesByRoom(roomId: String): Try[List[Message]]

  def getMessagesByRoomAndUser(roomId: String, userId: String): Try[List[Message]]

  def getMessagesByUser(userId: String): Try[List[Message]]

  def getRoomsByUser(userId: String): Try[List[Room]]

  def getUsersByRoom(roomId: String): Try[List[User]]

  def createUser(newUser: NewUser): Try[User]

  def createRoom(newRoom: NewRoom): Try[Room]

  def joinToRoom(roomId: String, userId: String): Try[Room]

  def leaveRoom(roomId: String, userId: String): Try[Room]

  def createMessage(roomId: String, userId: String, newMessage: NewMessage): Try[Message]

  def removeMessage(messageId: Id): Try[Message]

  def editMessage(messageId: Id, newMessage: NewMessage): Try[Message]

}

object DatabaseActor {

  case class GetUser(userId: Id)
  case class GetRoom(roomId: Id)
  case class GetMessage(messageId: Id)
  case class GetMessagesByRoom(roomId: Id)
  case class GetMessagesByRoomAndUser(roomId: Id, userId: Id)
  case class GetMessagesByUser(userId: Id)
  case class GetRoomsByUser(userId: Id)
  case class GetUsersByRoom(roomId: Id)
  case class CreateUser(newUser: NewUser)
  case class CreateRoom(newRoom: NewRoom)
  case class JoinToRoom(roomId: Id, userId: Id)
  case class LeaveRoom(roomId: Id, userId: Id)
  case class CreateMessage(roomId: Id, userId: Id, newMessage: NewMessage)
  case class RemoveMessage(messageId: Id)
  case class EditMessage(messageId: Id, newMessage: NewMessage)

}
