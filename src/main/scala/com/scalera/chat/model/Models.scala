package com.scalera.chat.model

import spray.json._
import java.util.Date

import EntityUtils._

object Models {

  case class User(
    name: String,
    id: Id = generateRandomId
  ) extends Entity

  case class NewUser(
    name: String
  )

  case class Room(
    name: String,
    users: Set[Id] = Set.empty[Id],
    id: Id = generateRandomId
  ) extends Entity

  case class NewRoom(
    name: String
  )

  case class Message(
    user: Id,
    room: Id,
    text: String,
    date: Long = getCurrentTime,
    id: Id = generateRandomId
  ) extends Entity {

    def MentionPrefix: Char = '@'
    def HashTagPrefix: Char = '#'

    def mentionRegex = getRegex(MentionPrefix)
    def hashtagRegex = getRegex(HashTagPrefix)

    def getRegex(prefix: Char) = (prefix + "(\\w)*").r

    def mentions: List[String] =
       mentionRegex findAllIn text toList

    def hashtags: List[String] =
       hashtagRegex findAllIn text toList

  }

  case class NewMessage(
    text: String
  )

  def getCurrentTime = new Date().getTime()
}

object ModelJsonFormats extends DefaultJsonProtocol {

  import Models._

  implicit val newUserJsonFormat = jsonFormat1(NewUser)
  implicit val newRoomJsonFormat = jsonFormat1(NewRoom)
  implicit val newMessageJsonFormat = jsonFormat1(NewMessage)

  implicit val userJsonFormat = jsonFormat2(User)
  implicit val roomJsonFormat = jsonFormat3(Room)

  implicit object MessageJsonFormat extends RootJsonFormat[Message] {

    def write(message: Message): JsValue =
      JsObject(
        "user" -> JsString(message.user),
        "room" -> JsString(message.room),
        "text" -> JsString(message.text),
        "date" -> JsNumber(message.date),
        "mentions" -> JsArray(message.mentions.map(_.toJson).toVector),
        "hashtags" -> JsArray(message.hashtags.map(_.toJson).toVector)
      )

    def read(value: JsValue ) =
      value match {
        case JsObject(fields) if fields.contains("user") && fields.contains("room") && fields.contains("text") =>
          Message(fields("user").toString, fields("room").toString, fields("text").toString)
        case _ =>
          throw new DeserializationException("Message expected")
      }
  }

}
