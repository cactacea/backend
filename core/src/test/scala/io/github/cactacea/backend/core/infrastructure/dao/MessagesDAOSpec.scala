package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class MessagesDAOSpec extends DAOSpec {


  feature("create") {

    scenario("should create a text message") {
      forOne(accountGen, accountGen, channelGen, textMessageGen) {
        (s, a1, g, m) =>
          // preparing
          //  session account is owner
          //  accountId1 is a member
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), accountCount, sessionId))
          val result = await(findMessage(messageId))
          assert(result.exists(_.id == messageId))
          assert(result.exists(_.messageType == MessageType.text))
          assert(result.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(result.exists(!_.contentWarning))
          assert(result.exists(_.accountCount == accountCount))
          assert(result.exists(_.contentStatus == ContentStatusType.unchecked))
          assert(result.exists(_.channelId == channelId))
          assert(result.exists(_.mediumId.isEmpty))
          assert(result.exists(_.readCount == 0))
      }
    }

    scenario("should create a medium message") {
      forOne(accountGen, accountGen, mediumGen) {
        (s, a1, i) =>

          // preparing
          //   session create a channel
          //   account1 join the channel
          //   session create a message
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val channelId = await(channelsDAO.create(sessionId))
          await(accountChannelsDAO.create(channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, mediumId, accountCount, sessionId))
          val result = await(findMessage(messageId))
          assert(result.exists(_.message.isEmpty))
          assert(result.exists(_.messageType == MessageType.medium))
          assert(result.exists(_.accountCount == 2))
          assert(result.exists(_.readCount == 0))
          assert(result.exists(_.mediumId.exists(_ == mediumId)))
      }
    }


    scenario("should create a system message") {
      forOne(accountGen, accountGen) {
        (s, a1) =>

          // preparing
          //   session create a channel
          //   account1 join the channel
          //   session create a message
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val channelId = await(channelsDAO.create(sessionId))
          await(accountChannelsDAO.create(channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, MessageType.joined, accountCount, sessionId))
          val result = await(findMessage(messageId))
          assert(result.exists(_.message.isEmpty))
          assert(result.exists(_.mediumId.isEmpty))
          assert(result.exists(_.messageType == MessageType.joined))
          assert(result.exists(_.accountCount == 2))
          assert(result.exists(_.readCount == 0))
      }
    }


  }

  feature("updateReadCount") {
    scenario("should should update read count") {
      forOne(accountGen, channelGen, messageGen) {
        (s, g, m) =>
          // preparing
          //  session account is owner
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), accountCount, sessionId))
          await(messagesDAO.updateReadCount(List(messageId)))
          val result = await(findMessage(messageId))
          assert(result.exists(_.id == messageId))
          assert(result.exists(_.accountCount == 1))
          assert(result.exists(_.readCount == 1))
      }

    }
  }

}


