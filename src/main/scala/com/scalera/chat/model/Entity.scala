package com.scalera.chat.model

import scala.util.Random

object EntityUtils {

  type Id = String

  val IdSize = 30

  val idGenerator = new Random()
  def generateRandomId =
    idGenerator.alphanumeric.take(IdSize).mkString

  trait Entity {
    val id: Id
  }
}
