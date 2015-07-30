package com.scalera.chat.routes

import spray.httpx.SprayJsonSupport._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import spray.routing.HttpService

import scala.util.Try

import com.scalera.chat.model.{ Models, ModelJsonFormats, EntityUtils }
import Models._
import ModelJsonFormats._
import EntityUtils._

import com.scalera.chat.database.DatabaseActor._

import com.wordnik.swagger.annotations._
import javax.ws.rs.{ Path, PathParam }

@Api(value = "/users", description = "Operations for users.")
trait UsersRoute extends HttpService { self: ChatRoute =>

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

  @ApiOperation(value = "Get user", httpMethod = "GET")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "userId",
        value = "User Id",
        required = true,
        dataType = "String",
        paramType = "path"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 404, message = "User not found"),
      new ApiResponse(code = 500, message = "Internal Server Error")
    )
  )
  def getUser(userId: Id) = get {
    complete {
      (databaseActor ? GetUser(userId)).mapTo[Option[User]]
    }
  }

  @ApiOperation(value = "Create user", httpMethod = "POST")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name = "user",
        value = "User to create",
        defaultValue = """{"name": "Monchito"}""",
        dataType = "NewUser",
        required = true,
        paramType = "body"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal Server Error")
    )
  )
  def createUser(newUser: NewUser) = post {
    complete {
      (databaseActor ? CreateUser(newUser)).mapTo[Try[User]]
    }
  }

  @ApiOperation(value = "Get messages of a user", httpMethod = "GET")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "userId",
        value = "User Id",
        required = true,
        dataType = "String",
        paramType = "path"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 404, message = "User not found"),
      new ApiResponse(code = 500, message = "Internal Server Error")
    )
  )
  @Path("/{userId}/messages")
  def getMessagesByUser(@PathParam("associateId") userId: Id) = get {
    complete {
      (databaseActor ? GetMessagesByUser(userId)).mapTo[Try[List[Message]]]
    }
  }

  @ApiOperation(value = "Get rooms of user", httpMethod = "GET")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "userId",
        value = "User Id",
        required = true,
        dataType = "String",
        paramType = "path"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 404, message = "User not found"),
      new ApiResponse(code = 500, message = "Internal Server Error")
    )
  )
  @Path("/{userId}/rooms")
  def getRoomsByUser(@PathParam("associateId") userId: Id) = get {
    complete {
      (databaseActor ? GetRoomsByUser(userId)).mapTo[Try[List[Room]]]
    }
  }
}
