package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.ChannelAuthorityType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class UserMessagesDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should fan out to channel members") {
      forOne(userGen, userGen, userGen, channelGen, messageGen) {
        (s, a1, a2, g, m) =>
          // preparing
          //  session user is owner
          //  userId1 is a member
          //  userId2 is not a member
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), userCount, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))

          // session user is member, so return true
          assert(existsUserMessage(messageId, sessionId.userId))

          // user1 is member, so return true
          assert(existsUserMessage(messageId, userId1))

          // user2 is not member, so return false
          assert(!existsUserMessage(messageId, userId2))
      }
    }

    scenario("should return an exception occurs if duplication") {
      forOne(userGen, userGen, channelGen, messageGen) {
        (s, a1, g, m) =>
          // preparing
          //  session user is owner
          //  userId1 is a member
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), userCount, sessionId))

          // exception occurs
          await(userMessagesDAO.create(channelId, messageId, sessionId))
          assert(intercept[ServerError] {
            await(userMessagesDAO.create(channelId, messageId, sessionId))
          }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should delete an user messages") {
      forOne(userGen, userGen, userGen, channelGen, messageGen) {
        (s, a1, a2, g, m) =>
          // preparing
          //  session user is owner
          //  userId1 is a member
          //  userId2 is a member
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          await(userChannelsDAO.create(userId2, channelId, ChannelAuthorityType.member, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), userCount, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))

          // session user is member, so return true
          assert(existsUserMessage(messageId, sessionId.userId))

          // user1 is member, so return true
          assert(existsUserMessage(messageId, userId1))

          // user2 is member, so return true
          assert(existsUserMessage(messageId, userId2))

          await(userMessagesDAO.delete(userId1, channelId))
          await(userMessagesDAO.delete(userId2, channelId))

          // session user messages will not be deleted, so return true
          assert(existsUserMessage(messageId, sessionId.userId))

          // user1 messages will be deleted, so return false
          assert(!existsUserMessage(messageId, userId1))

          // user2 messages will be deleted, so return false
          assert(!existsUserMessage(messageId, userId2))
      }
    }
  }

  feature("find a message") {
      forOne(userGen, userGen, userGen, channelGen, mediumGen) {
        (s, a1, a2, g, i) =>
          // preparing
          //  session user is owner
          //  userId1 is a member
          //  userId2 is not a member
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, mediumId, userCount, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))

          // session user is member, so return a message
          val result1 = await(userMessagesDAO.find(messageId, sessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == messageId))
          assert(result1.exists(_.medium.exists(_.id == mediumId)))

          // user1 is member, so return a message
          val result2 = await(userMessagesDAO.find(messageId, userId1.sessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.medium.exists(_.id == mediumId)))

          // user2 is not member, so no return
          val result3 = await(userMessagesDAO.find(messageId, userId2.sessionId))
          assert(result3.isEmpty)
      }
  }

  feature("find messages") {
    scenario("should return user messages") {
      forAll(userGen, userGen, channelGen, message20ListGen, booleanGen) { (s, a1, g, l, ascending) =>

        // preparing
        //  session user is owner
        //  userId1 is not a member
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))

        val userCount = await(channelsDAO.findUserCount(channelId))

        // create message
        val messages = l.map({ case (m, i) =>
          val mediumId = i.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
          val messageId = mediumId match {
            case Some(id) =>
              await(messagesDAO.create(channelId, id, userCount, sessionId))
            case None =>
              await(messagesDAO.create(channelId, m.message.getOrElse(""), userCount, sessionId))
          }
          await(userMessagesDAO.create(channelId, messageId, sessionId))
          m.copy(id = messageId, mediumId = mediumId)
        })

        // sorting
        val createdMessages = if (ascending) {
          messages
        } else {
          messages.reverse
        }

        // should return first page
        val result1 = await(userMessagesDAO.find(channelId, None, 0, 10, ascending, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i).id)
          assert(m.medium.map(_.id) == createdMessages(i).mediumId)
        })

        // should return next page
        val size2 = result1.size
        val result2 = await(userMessagesDAO.find(channelId, result1.lastOption.map(_.next), 0, 10, ascending, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i + size2).id)
          assert(m.medium.map(_.id) == createdMessages(i + size2).mediumId)
        })

        // should not return
        val result3 = await(userMessagesDAO.find(channelId, result2.lastOption.map(_.next), 0, 10, ascending, sessionId))
        assert(result3.size == 0)


        // should not return by no member user
        val result4 = await(userMessagesDAO.find(channelId, None, 0, 10, ascending, userId1.sessionId))
        assert(result4.size == 0)

      }
    }

  }

  feature("updateUnread") {
    scenario("should update unread to true") {
      forOne(userGen, userGen, channelGen, messageGen) {
        (s, a1, g, m) =>

          // preparing
          //  session user is owner
          //  userId1 is a member
          //  userId2 is not a member
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), userCount, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))

          // user1 read a message
          await(userMessagesDAO.updateUnread(List(messageId), userId1.sessionId))

          // session user is member, so return a message
          val result1 = await(userMessagesDAO.find(messageId, sessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == messageId))
          assert(result1.exists(_.unread))

          // user1 is member, so return a message
          val result2 = await(userMessagesDAO.find(messageId, userId1.sessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.id == messageId))
          assert(result2.exists(!_.unread))

      }

    }
  }



}
