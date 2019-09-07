package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ContentStatusType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class MessagesDAOSpec extends DAOSpec {


  feature("create") {

    scenario("should create a text message") {
      forOne(userGen, userGen, channelGen, textMessageGen) {
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
          val result = await(findMessage(messageId))
          assert(result.exists(_.id == messageId))
          assert(result.exists(_.messageType == MessageType.text))
          assert(result.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(result.exists(!_.contentWarning))
          assert(result.exists(_.userCount == userCount))
          assert(result.exists(_.contentStatus == ContentStatusType.unchecked))
          assert(result.exists(_.channelId == channelId))
          assert(result.exists(_.mediumId.isEmpty))
          assert(result.exists(_.readCount == 0))
      }
    }

    scenario("should create a medium message") {
      forOne(userGen, userGen, mediumGen) {
        (s, a1, i) =>

          // preparing
          //   session create a channel
          //   user1 join the channel
          //   session create a message
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val channelId = await(channelsDAO.create(sessionId))
          await(userChannelsDAO.create(channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, mediumId, userCount, sessionId))
          val result = await(findMessage(messageId))
          assert(result.exists(_.message.isEmpty))
          assert(result.exists(_.messageType == MessageType.medium))
          assert(result.exists(_.userCount == 2))
          assert(result.exists(_.readCount == 0))
          assert(result.exists(_.mediumId.exists(_ == mediumId)))
      }
    }


    scenario("should create a system message") {
      forOne(userGen, userGen) {
        (s, a1) =>

          // preparing
          //   session create a channel
          //   user1 join the channel
          //   session create a message
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val channelId = await(channelsDAO.create(sessionId))
          await(userChannelsDAO.create(channelId, ChannelAuthorityType.organizer, sessionId))
          await(userChannelsDAO.create(userId1, channelId, ChannelAuthorityType.member, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, MessageType.joined, userCount, sessionId))
          val result = await(findMessage(messageId))
          assert(result.exists(_.message.isEmpty))
          assert(result.exists(_.mediumId.isEmpty))
          assert(result.exists(_.messageType == MessageType.joined))
          assert(result.exists(_.userCount == 2))
          assert(result.exists(_.readCount == 0))
      }
    }


  }

  feature("updateReadCount") {
    scenario("should should update read count") {
      forOne(userGen, channelGen, messageGen) {
        (s, g, m) =>
          // preparing
          //  session user is owner
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(sessionId.userId, channelId, ChannelAuthorityType.organizer, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), userCount, sessionId))
          await(messagesDAO.updateReadCount(Seq(messageId)))
          val result = await(findMessage(messageId))
          assert(result.exists(_.id == messageId))
          assert(result.exists(_.userCount == 1))
          assert(result.exists(_.readCount == 1))
      }

    }
  }

}


