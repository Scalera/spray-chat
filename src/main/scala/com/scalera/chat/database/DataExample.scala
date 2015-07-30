package com.scalera.chat.database

import com.scalera.chat.model.Models._

object DataExample {

  val user1 = User(name = "Peter", id = "peter")

  val user2 = User(name = "John", id = "john")

  val user3 = User(name = "Chris", id = "chris")

  val user4 = User(name = "Bob", id = "bob")

  val exampleUsers = Map(
      user1.id -> user1,
      user2.id -> user2,
      user3.id -> user3,
      user4.id -> user4
    )

  val room1 = Room(name = "General", users = Set(user1.id, user2.id, user3.id, user4.id), id = "general")

  val room2 = Room(name = "PartyHard", users = Set(user1.id, user2.id), id = "PartyHard")

  val exampleRooms = Map(
      room1.name -> room1,
      room2.name -> room2
    )

  val message1 = Message(user = user1.id, room = room1.name, text = "Hi Guys!")

  val message2 = Message(user = user2.id, room = room1.name, text = "Hi!")

  val message3 = Message(user = user3.id, room = room1.name, text = "Wololo! #concept")

  val message4 = Message(user = user1.id, room = room2.name, text = s"Hi @${user2.name} !")

  val message5 = Message(user = user2.id, room = room2.name, text = "Hi! What's up?")

  val message6 = Message(user = user4.id, room = room1.name, text = s"Hi @${user1.name} @${user2.name} @${user3.name}")

  val message7 = Message(user = user2.id, room = room1.name, text = "Lunch break! I will be out")

  val message8 = Message(user = user1.id, room = room2.name, text = "A party tonight? #yeah")

  val message9 = Message(user = user3.id, room = room1.name, text = "Ok!")

  val message10 = Message(user = user4.id, room = room1.name, text = s"@${user2.name} Bye!")

  val exampleMessages = Map(
      message1.id -> message1,
      message2.id -> message2,
      message3.id -> message3,
      message4.id -> message4,
      message5.id -> message5,
      message6.id -> message6,
      message7.id -> message7,
      message8.id -> message8,
      message9.id -> message9,
      message10.id -> message10
    )

}