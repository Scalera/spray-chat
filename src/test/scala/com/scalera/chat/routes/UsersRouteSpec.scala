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

class UsersRouteSpec extends WordSpec
  with Matchers
  with BeforeAndAfterEach
  with ScalatestRouteTest
  with HttpService
  with UsersRoute
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
      name  = "FooRoom"
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

  "The user route" when {

    "GET user" should {

      "return a user" in {

        database.users = dummyUsers

        Get(s"/users/$userId") ~> usersRoute ~> check {
          status should equal (OK)
          responseAs[User] === dummyUser
        }
      }

      "return a 404 Not Found when doesn't exist the user" in {

        database.users = Map.empty[Id, User]

        Get(s"/users/$userId") ~> usersRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "GET messages of a user" should {

      "return the messages of a user" in {

        database.users = dummyUsers
        database.messages = dummyMessages

        Get(s"/users/$userId/messages") ~> usersRoute ~> check {
          status should equal (OK)
          responseAs[List[Message]] === List(dummyMessage)
        }
      }

      "return an empty list when user has no messages" in {

        database.users = dummyUsers
        database.messages = Map.empty[Id, Message]

        Get(s"/users/$userId/messages") ~> usersRoute ~> check {
          status should equal (OK)
          responseAs[List[Message]] === List.empty[Message]
        }
      }

      "return a 404 Not Found when doesn't exist the user" in {

        database.users = Map.empty[Id, User]
        database.messages = dummyMessages

        Get(s"/users/$userId/messages") ~> usersRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "GET rooms of a user" should {

      "return the rooms of a user" in {

        database.users = dummyUsers
        database.rooms = dummyRooms

        Get(s"/users/$userId/rooms") ~> usersRoute ~> check {
          status should equal (OK)
          responseAs[List[Room]] === List(dummyRoom)
        }
      }

      "return an empty list when user has no rooms" in {

        database.users = dummyUsers
        database.rooms = Map.empty[Id, Room]

        Get(s"/users/$userId/rooms") ~> usersRoute ~> check {
          status should equal (OK)
          responseAs[List[Room]] === List()
        }
      }

      "return a 404 Not Found when doesn't exist the user" in {

        database.users = Map.empty[Id, User]
        database.rooms = dummyRooms

        Get(s"/users/$userId/rooms") ~> usersRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "POST user" should {

      val newDummyUser = NewUser("Foo")

      "create a new user" in {

        Post(s"/users", newDummyUser) ~> usersRoute ~> check {
          status should equal (OK)
          responseAs[User].name === newDummyUser.name
        }

        database.users.size should equal (1)

      }
    }
  }
}
