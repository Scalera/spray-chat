package com.scalera.chat.routes

import spray.http.StatusCodes._
import spray.util.LoggingContext
import spray.routing._
import Directives._

import com.scalera.chat.model.Exceptions._

object CustomExceptionHandler {

  implicit def exceptionHandler(implicit log: LoggingContext) = ExceptionHandler {
    case e: EntityNotFound =>
      complete((NotFound, e.getMessage))
    case e: EntityAlreadyExists =>
      complete((Conflict, e.getMessage))
  }
}
