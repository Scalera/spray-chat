package com.scalera.chat.routes

import spray.testkit.ScalatestRouteTest
import spray.routing.HttpService
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._

import akka.testkit.TestActorRef

import org.scalatest._

import com.scalera.chat.database.InMemoryDatabaseActor
import com.scalera.chat.model.{ Models, ModelJsonFormats, EntityUtils }
import Models._
import ModelJsonFormats._
import EntityUtils._
import CustomExceptionHandler._

class RoomsRouteSpec extends WordSpec
  with Matchers
  with BeforeAndAfterEach
  with ScalatestRouteTest
  with HttpService
  with RoomsRoute
  with ChatRoute{

  def actorRefFactory = system

  val databaseActor = TestActorRef[InMemoryDatabaseActor]
  val database = databaseActor.underlyingActor

  val messageId = "1"
  val userId = "1"
  val roomId = "1"

  val dummyUser =
    User(
      id    = userId,
      name  = "FooName"
    )

  val dummyRoom =
    Room(
      id    = roomId,
      name  = "Foo-Room",
      users  = Set(userId)
    )

  val dummyMessage =
    Message(
      id    = messageId,
      user  = userId,
      room  = roomId,
      text  = "Foo"
    )

  val dummyUsers = Map(dummyUser.id -> dummyUser)
  val dummyRooms = Map(dummyRoom.id -> dummyRoom)
  val dummyMessages = Map(dummyMessage.id -> dummyMessage)

  "The room route" when {

    "GET room" should {

      "return a room" in {

        database.rooms = dummyRooms

        Get(s"/rooms/$roomId") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[Room] === dummyRoom
        }
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]

        Get(s"/rooms/$roomId") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "POST room" should {

      val newDummyRoom = NewRoom("Foo-Room")

      "create a new room" in {

        Post(s"/rooms", newDummyRoom) ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[Room].name === newDummyRoom.name
        }

        database.rooms.size should equal (1)

      }
    }

    "GET messages of a room" should {

      "return the messages of a room" in {

        database.rooms = dummyRooms
        database.messages = dummyMessages

        Get(s"/rooms/$roomId/messages") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[List[Message]] === dummyMessages
        }
      }

      "return empty list when the room has no messages" in {

        database.rooms = dummyRooms

        Get(s"/rooms/$roomId/messages") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[List[Message]] === List.empty[Message]
        }
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]
        database.messages = dummyMessages

        Get(s"/rooms/$roomId/messages") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "GET users of a room" should {

      "return the users of a room" in {

        database.rooms = dummyRooms
        database.users = dummyUsers

        Get(s"/rooms/$roomId/users") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[List[User]] === dummyUsers
        }
      }

      "return empty list when the room has no users" in {

        database.rooms = dummyRooms

        Get(s"/rooms/$roomId/users") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[List[User]] === List.empty[User]
        }
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]
        database.users = dummyUsers

        Get(s"/rooms/$roomId/users") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "GET messages of a user in a room" should {

      "return the messages of a user in a room" in {

        database.rooms = dummyRooms
        database.users = dummyUsers
        database.messages = dummyMessages

        Get(s"/rooms/$roomId/users/$userId/messages") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[List[Message]] === dummyMessages
        }
      }

      "return empty list when the user has no messages in the room" in {

        database.rooms = dummyRooms
        database.users = dummyUsers
        database.messages = Map.empty[Id, Message]

        Get(s"/rooms/$roomId/users/$userId/messages") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[List[Message]] === List.empty[Message]
        }
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]
        database.users = dummyUsers
        database.messages = dummyMessages

        Get(s"/rooms/$roomId/users/$userId/messages") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }

      "return a 404 Not Found when doesn't exist the user in the rom" in {

        database.rooms = dummyRooms
        database.users = Map.empty[Id, User]
        database.messages = dummyMessages

        Get(s"/rooms/$roomId/users/$userId/messages") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "POST user in a room" should {

      "join a user in a room" in {

        database.rooms = Map(dummyRoom.id -> dummyRoom.copy(users = Set.empty[Id]))
        database.users = dummyUsers

        database.rooms(dummyRoom.id).users.size should equal (0)

        Post(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[Room] === dummyRoom
        }

        database.rooms(dummyRoom.id).users.size should equal (1)
      }

      "return 409 Conflict when the user already exists in the room" in {

        database.rooms = dummyRooms
        database.users = dummyUsers

        Post(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (Conflict)
        }
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]
        database.users = dummyUsers

        Post(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }

      "return a 404 Not Found when doesn't exist the user in the room" in {

        database.rooms = dummyRooms
        database.users = Map.empty[Id, User]

        Post(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "DELETE user in a room" should {

      "leave a user in a room" in {

        database.rooms = dummyRooms
        database.users = dummyUsers

        database.rooms(dummyRoom.id).users.size should equal (1)

        Delete(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[Room] === dummyRoom
        }

        database.rooms(dummyRoom.id).users.size should equal (0)
      }

      "return 404 Not Found when the user doesn't exist in the room" in {

        database.rooms = Map(dummyRoom.id -> dummyRoom.copy(users = Set.empty[Id]))
        database.users = dummyUsers

        Delete(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]
        database.users = dummyUsers

        Delete(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }

      "return a 404 Not Found when doesn't exist the user in the room" in {

        database.rooms = dummyRooms
        database.users = Map.empty[Id, User]

        Delete(s"/rooms/$roomId/users/$userId") ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "POST message in a room" should {

      val newDummyMessage = NewMessage("Foo")

      "create a new message in a room" in {

        database.rooms = dummyRooms
        database.users = dummyUsers
        database.messages = dummyMessages

        database.messages.size should equal (1)

        Post(s"/rooms/$roomId/users/$userId/messages", newDummyMessage) ~> roomsRoute ~> check {
          status should equal (OK)
          responseAs[Message] === dummyMessage
        }

        database.messages.size should equal (2)
      }

      "return a 404 Not Found when doesn't exist the room" in {

        database.rooms = Map.empty[Id, Room]
        database.users = dummyUsers
        database.messages = dummyMessages

        Post(s"/rooms/$roomId/users/$userId/messages", newDummyMessage) ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }

      "return a 404 Not Found when doesn't exist the user in the room" in {

        database.rooms = dummyRooms
        database.users = Map.empty[Id, User]
        database.messages = dummyMessages

        Post(s"/rooms/$roomId/users/$userId/messages", newDummyMessage) ~> roomsRoute ~> check {
          status should equal (NotFound)
        }
      }
    }
  }
}
