package com.scalera.chat.database

import akka.actor.{ Actor, Props }
import scala.util.Try

import com.scalera.chat.model.{ Models, EntityUtils, Exceptions }
import Models._
import EntityUtils._
import Exceptions._

class InMemoryDatabaseActor extends DatabaseActor {

  import DataExample._

  var users: Map[Id, User] = exampleUsers //Map[Id, User]()

  var rooms: Map[Id, Room] = exampleRooms //Map[Id, Room]()

  var messages: Map[Id, Message] = exampleMessages //Map[Id, Message]()

  def getUser(userId: Id) =
    users.get(userId)

  def getRoom(roomId: Id) =
    rooms.get(roomId)

  def getMessage(messageId: Id) =
    messages.get(messageId)

  def getMessagesByRoom(roomId: Id) =
    Try(
      getRoom(roomId).fold(
        throw EntityNotFound(roomId)
      ){ room =>
        messages.values.filter(_.room == roomId).toList
      }
    )

  def getMessagesByRoomAndUser(roomId: Id, userId: Id) =
    Try(
      getRoom(roomId).fold(
        throw EntityNotFound(roomId)
      ){ room =>
        getUser(userId).fold(
          throw EntityNotFound(userId)
        ){ user =>
          messages.values.filter(_.room == roomId).filter(_.user == userId).toList
        }
      }
    )

  def getMessagesByUser(userId: Id): Try[List[Message]] =
    Try(
      getUser(userId).fold(
        throw EntityNotFound(userId)
      ){ user =>
        messages.values.filter(_.user == userId).toList
      }
    )

  def getRoomsByUser(userId: Id) =
    Try(
      getUser(userId).fold(
        throw EntityNotFound(userId)
      ){ user =>
        rooms.values.filter(_.users.contains(userId)).toList
      }
    )

  def getUsersByRoom(roomId: Id) =
    Try(
      getRoom(roomId).fold(
        throw EntityNotFound(roomId)
      ){ room =>
        for {
          userId  <- room.users.toList
          user    <- users.get(userId)
        } yield user
      }
    )

  def createUser(newUser: NewUser) =
    Try {
      val user = User(newUser.name)
      users = users + (user.id -> user)
      user
    }

  def createRoom(newRoom: NewRoom) =
    Try {
      val room = Room(newRoom.name)
      rooms = rooms + (room.id -> room)
      room
    }

  def joinToRoom(roomId: Id, userId: Id) =
    Try {
      getRoom(roomId).fold(
        throw EntityNotFound(roomId)
      ) { room =>
        getUser(userId).fold(
          throw EntityNotFound(userId)
        ){ user =>
          if(room.users.contains(userId))
            throw EntityAlreadyExists(userId)
          else {
            val roomUpdated = room.copy(users = room.users + userId)
            rooms = rooms + (roomId -> roomUpdated)
            roomUpdated
          }
        }
      }
    }

  def leaveRoom(roomId: Id, userId: Id) =
    Try {
      getRoom(roomId).fold(
        throw EntityNotFound(roomId)
      ) { room =>
        getUser(userId).fold(
          throw EntityNotFound(userId)
        ){ user =>
          if(room.users.contains(userId)) {
            val roomUpdated = room.copy(users = room.users - userId)
            rooms = rooms + (roomId -> roomUpdated)
            roomUpdated
          } else
            throw EntityNotFound(userId)
        }
      }
    }

  def createMessage(roomId: Id, userId: Id, newMessage: NewMessage) =
    Try {
      getRoom(roomId).fold(
        throw EntityNotFound(roomId)
      ) { room =>
        getUser(userId).fold(
          throw EntityNotFound(userId)
        ){ user =>
          val message = Message(userId, roomId, newMessage.text)
          messages = messages + (message.id -> message)
          message
        }
      }
    }

  def removeMessage(messageId: Id) =
    Try {
      getMessage(messageId).fold(
        throw EntityNotFound(messageId)
      ) { message =>
        messages = messages - messageId
        message
      }
    }

  def editMessage(messageId: Id, newMessage: NewMessage) =
    Try {
      getMessage(messageId).fold(
        throw EntityNotFound(messageId)
      ) { message =>
        val messageUpdated = message.copy(text = newMessage.text)
        messages = messages + (messageId -> messageUpdated)
        messageUpdated
      }
    }
}

object InMemoryDatabaseActor {

  def props = Props[InMemoryDatabaseActor]

}
