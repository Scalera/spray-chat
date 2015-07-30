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

class MessagesRouteSpec extends WordSpec
  with Matchers
  with BeforeAndAfterEach
  with ScalatestRouteTest
  with HttpService
  with MessagesRoute
  with ChatRoute{

  def actorRefFactory = system

  val databaseActor = TestActorRef[InMemoryDatabaseActor]
  val database = databaseActor.underlyingActor

  val messageId = "1"
  val userId = "1"
  val roomId = "1"

  val dummyMessage =
    Message(
      id    = messageId,
      user  = userId,
      room  = roomId,
      text  = "Foo"
    )

  val dummyMessages = Map(dummyMessage.id -> dummyMessage)

  "The message route" when {

    "GET message" should {

      "return a message" in {

        database.messages = dummyMessages

        Get(s"/messages/$messageId") ~> messagesRoute ~> check {
          status should equal (OK)
          responseAs[Message] === dummyMessage
        }
      }

      "return a 404 Not Found when doesn't exist the message" in {

        database.messages = Map.empty[Id, Message]

        Get(s"/messages/$messageId") ~> messagesRoute ~> check {
          status should equal (NotFound)
        }
      }
    }

    "DELETE message" should {

      "remove a message" in {

        database.messages = dummyMessages

        Delete(s"/messages/$messageId") ~> messagesRoute ~> check {
          status should equal (OK)
          responseAs[Message] === dummyMessage
        }

        database.messages.isEmpty should equal (true)

      }

      "return a 404 Not Found when doesn't exist the message" in {

        database.messages = Map.empty[Id, Message]

        Delete(s"/messages/$messageId") ~> messagesRoute ~> check {
          status should equal (NotFound)
        }

        database.messages.isEmpty should equal (true)
      }
    }

    "PUT message" should {

      val newDummyMessage = NewMessage("Boo")

      "update a message" in {

        database.messages = dummyMessages

        Put(s"/messages/$messageId", newDummyMessage) ~> messagesRoute ~> check {
          status should equal (OK)
          responseAs[Message] === dummyMessage.copy(text = newDummyMessage.text)
        }

        database.messages.size should equal (1)

      }

      "return a 404 Not Found when doesn't exist the message" in {

        database.messages = Map.empty[Id, Message]

        Put(s"/messages/$messageId", newDummyMessage) ~> messagesRoute ~> check {
          status should equal (NotFound)
        }

        database.messages.isEmpty should equal (true)
      }
    }
  }
}
