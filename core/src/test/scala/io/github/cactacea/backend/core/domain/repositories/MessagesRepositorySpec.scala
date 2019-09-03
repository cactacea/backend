package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, MediumId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserNotJoined, AuthorityNotFound}

class MessagesRepositorySpec extends RepositorySpec {

  feature("createText") {

    scenario("should create a message") {
      forOne(userGen, userGen, everyoneChannelGen, textMessageGen) {
        (s, a1, g, m) =>
          // preparing
          //  session user is owner
          //  userId1 is a member
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(channelUsersRepository.create(userId1, channelId, sessionId))
          val result = await(messagesRepository.createText(channelId, m.message.getOrElse(""), sessionId))
          assert(result.messageType == MessageType.text)
          assert(result.message.getOrElse("") == m.message.getOrElse(""))
          assert(!result.warning)
          assert(!result.rejected)
          assert(result.userCount == 2)
          assert(result.channelId == channelId)
          assert(result.medium.isEmpty)
          assert(result.readUserCount == 0)
      }
    }

    scenario("should create a user message") {
      forOne(userGen, user20ListGen, everyoneChannelGen, textMessageGen) {
        (s, l, g, m) =>
          val sessionId = await(createUser(s.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val userIds = l.map({ a =>
            val userId = await(createUser(a.userName)).id
            await(channelUsersRepository.create(userId, channelId, sessionId))
            userId
          })

          val message = await(messagesRepository.createText(channelId, m.message.getOrElse(""), sessionId))
          userIds.zipWithIndex.foreach({ case (userId, _) =>
            val result = await(messagesRepository.find(channelId, None, 0, 10, false, userId.sessionId))
            assert(result.headOption.exists(_.id == message.id))
          })

      }
    }

    scenario("should exception if not joined to a channel") {
      forOne(userGen, userGen, everyoneChannelGen, textMessageGen) {
        (s, a1, g, m) =>
          // preparing
          //  session user is owner
          //  userId1 is a not member
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // should return exception by no member user
          assert(intercept[CactaceaException] {
            await(messagesRepository.createText(channelId, m.message.getOrElse(""), userId1.sessionId))
          }.error == UserNotJoined)

      }
    }
  }

  feature("createMedium") {

    scenario("should create a message") {
      forOne(userGen, userGen, everyoneChannelGen, mediumGen) {
        (s, a1, g, i) =>

          // preparing
          //   session create a channel
          //   user1 join the channel
          //   session create a message
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(channelUsersRepository.create(userId1, channelId, sessionId))
          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val result = await(messagesRepository.createMedium(channelId, mediumId, sessionId))
          assert(result.message.isEmpty)
          assert(result.messageType == MessageType.medium)
          assert(result.userCount == 2)
          assert(result.readUserCount == 0)
          assert(result.medium.exists(_.id == mediumId))
      }
    }

    scenario("should create a user message") {
      forOne(userGen, user20ListGen, everyoneChannelGen, mediumGen) {
        (s, l, g, i) =>
          val sessionId = await(createUser(s.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val userIds = l.map({ a =>
            val userId = await(createUser(a.userName)).id
            await(channelUsersRepository.create(userId, channelId, sessionId))
            userId
          })

          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val message = await(messagesRepository.createMedium(channelId, mediumId, sessionId))

          userIds.zipWithIndex.foreach({ case (userId, _) =>
            val result = await(messagesRepository.find(channelId, None, 0, 10, false, userId.sessionId))
            assert(result.headOption.exists(_.id == message.id))
          })

      }
    }

    scenario("should exception if not joined to a channel") {
      forOne(userGen, userGen, everyoneChannelGen, mediumGen) {
        (s, a1, g, i) =>
          // preparing
          //  session user is owner
          //  userId1 is a not member
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // should return exception by no member user
          assert(intercept[CactaceaException] {
            val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, userId1.sessionId))
            await(messagesRepository.createMedium(channelId, mediumId, userId1.sessionId))

          }.error == UserNotJoined)

      }
    }


    scenario("should exception if a medium not exist") {
      forOne(userGen, userGen, everyoneChannelGen) {
        (s, a1, g) =>
          // preparing
          //  session user is owner
          //  userId1 is a not member
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a1.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(channelUsersRepository.create(userId, channelId, sessionId))

          // result
          assert(intercept[CactaceaException] {
            await(messagesRepository.createMedium(channelId, MediumId(0), userId.sessionId))

          }.error == AuthorityNotFound)

      }
    }

  }


  feature("delete") {
    scenario("should delete an user message") {
      forOne(userGen, channelGen, messageGen) {
        (s, g, m) =>
          val sessionId = await(createUser(s.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val messageId = await(messagesRepository.createText(channelId, m.message.getOrElse(""), sessionId)).id
          await(messagesRepository.delete(channelId, sessionId))
          val result = await(userMessagesDAO.find(messageId, sessionId))
          assert(result.isEmpty)
      }
    }
  }

  feature("find") {

    scenario("should return message list") {
      forOne(userGen, userGen, everyoneChannelGen, message20ListGen, booleanGen) { (s, a1, g, l, ascending) =>

        // preparing
        //  session user is owner
        //  userId is not a member
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId, channelId, sessionId))

        // create message
        val messages = l.map({ case (m, i) =>
          val mediumId = i.map(m => await(mediumsRepository.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
          val messageId = mediumId match {
            case Some(id) =>
              await(messagesRepository.createMedium(channelId, id, sessionId)).id
            case None =>
              await(messagesRepository.createText(channelId, m.message.getOrElse(""), sessionId)).id
          }
          m.copy(id = messageId, mediumId = mediumId)
        })

        // sorting
        val createdMessages = if (ascending) {
          messages
        } else {
          messages.reverse
        }

        //   first message is system message so offset set to 1
        val offset = if (ascending) { 1 } else { 0 }
        val lessCount = if (ascending) { 0 } else { 1 }

        // should return first page
        val result1 = await(messagesRepository.find(channelId, None, offset, 10, ascending, userId.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i).id)
          assert(m.medium.map(_.id) == createdMessages(i).mediumId)
        })

        // should return next page
        val size2 = result1.size
        val result2 = await(messagesRepository.find(channelId, result1.lastOption.map(_.next), 0, 10, ascending, userId.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i + size2).id)
          assert(m.medium.map(_.id) == createdMessages(i + size2).mediumId)
        })

        // should not return
        val result3 = await(messagesRepository.find(channelId, result2.lastOption.map(_.next), 0, 10, ascending, userId.sessionId))
        assert(result3.size == lessCount)



      }

    }

    scenario("should update unread count") {
      forOne(userGen, user20ListGen, everyoneChannelGen, mediumGen) {
        (s, l, g, i) =>
          val sessionId = await(createUser(s.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val userIds = l.map({ a =>
            val userId = await(createUser(a.userName)).id
            await(channelUsersRepository.create(userId, channelId, sessionId))
            userId
          })

          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val message = await(messagesRepository.createMedium(channelId, mediumId, sessionId))

          userIds.zipWithIndex.foreach({ case (userId, i) =>
            val result = await(messagesRepository.find(channelId, None, 0, 10, false, userId.sessionId))
            assert(result.headOption.exists(_.id == message.id))
            assert(result.headOption.exists(_.userCount == message.userCount))
            assert(result.headOption.exists(_.readUserCount == i))
          })

      }
    }

    scenario("should update unread status") {
      forOne(userGen, user20ListGen, everyoneChannelGen, mediumGen) {
        (s, l, g, i) =>
          val sessionId = await(createUser(s.userName)).id.sessionId
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val userIds = l.map({ a =>
            val userId = await(createUser(a.userName)).id
            await(channelUsersRepository.create(userId, channelId, sessionId))
            userId
          })

          val mediumId = await(mediumsRepository.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val message = await(messagesRepository.createMedium(channelId, mediumId, sessionId))

          userIds.zipWithIndex.foreach({ case (userId, i) =>
            await(messagesRepository.find(channelId, None, 0, 10, false, userId.sessionId))
            val result = await(messagesRepository.find(channelId, None, 0, 10, false, userId.sessionId))
            assert(result.headOption.exists(_.id == message.id))
            assert(result.headOption.exists(_.userCount == message.userCount))
            assert(result.headOption.exists(_.readUserCount == 1 + i))
            assert(result.headOption.exists(!_.unread))
          })

      }
    }

    scenario("should return exception if a channel not exist") {
      forOne(userGen) {
        (s) =>
          // preparing
          //  session user is owner
          val sessionId = await(createUser(s.userName)).id.sessionId

          // should return exception by no member user
          assert(intercept[CactaceaException] {
            await(messagesRepository.find(ChannelId(0), None, 0, 10, false, sessionId))
          }.error == UserNotJoined)

      }
    }

    scenario("should return exception if an user not joined to a channel") {
      forOne(userGen, userGen, everyoneChannelGen) {
        (s, a1, g) =>
          // preparing
          //  session user is owner
          //  userId1 is a not member
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId = await(createUser(a1.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

          // should return exception by no member user
          assert(intercept[CactaceaException] {
            await(messagesRepository.find(channelId, None, 0, 10, false, userId.sessionId))
          }.error == UserNotJoined)

      }
    }
  }

}

