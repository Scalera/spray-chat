package com.scalera.chat.model

import com.scalera.chat.model.EntityUtils._

object Exceptions {

  case class EntityNotFound(id: Id) extends RuntimeException(
    s"Entity with id $id doesn't exist"
  )

  case class EntityAlreadyExists(id: Id) extends RuntimeException(
    s"Entity with id $id already exists"
  )

}
