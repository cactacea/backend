package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class AccountMessagesDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should fan out to channel members") {
      forOne(accountGen, accountGen, accountGen, channelGen, messageGen) {
        (s, a1, a2, g, m) =>
          // preparing
          //  session account is owner
          //  accountId1 is a member
          //  accountId2 is not a member
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), accountCount, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))

          // session account is member, so return true
          assert(existsAccountMessage(messageId, sessionId.toAccountId))

          // account1 is member, so return true
          assert(existsAccountMessage(messageId, accountId1))

          // account2 is not member, so return false
          assert(!existsAccountMessage(messageId, accountId2))
      }
    }

    scenario("should return an exception occurs if duplication") {
      forOne(accountGen, accountGen, channelGen, messageGen) {
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

          // exception occurs
          await(accountMessagesDAO.create(channelId, messageId, sessionId))
          assert(intercept[ServerError] {
            await(accountMessagesDAO.create(channelId, messageId, sessionId))
          }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should delete an account messages") {
      forOne(accountGen, accountGen, accountGen, channelGen, messageGen) {
        (s, a1, a2, g, m) =>
          // preparing
          //  session account is owner
          //  accountId1 is a member
          //  accountId2 is a member
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          await(accountChannelsDAO.create(accountId2, channelId, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), accountCount, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))

          // session account is member, so return true
          assert(existsAccountMessage(messageId, sessionId.toAccountId))

          // account1 is member, so return true
          assert(existsAccountMessage(messageId, accountId1))

          // account2 is member, so return true
          assert(existsAccountMessage(messageId, accountId2))

          await(accountMessagesDAO.delete(accountId1, channelId))
          await(accountMessagesDAO.delete(accountId2, channelId))

          // session account messages will not be deleted, so return true
          assert(existsAccountMessage(messageId, sessionId.toAccountId))

          // account1 messages will be deleted, so return false
          assert(!existsAccountMessage(messageId, accountId1))

          // account2 messages will be deleted, so return false
          assert(!existsAccountMessage(messageId, accountId2))
      }
    }
  }

  feature("find a message") {
      forOne(accountGen, accountGen, accountGen, channelGen, mediumGen) {
        (s, a1, a2, g, i) =>
          // preparing
          //  session account is owner
          //  accountId1 is a member
          //  accountId2 is not a member
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, mediumId, accountCount, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))

          // session account is member, so return a message
          val result1 = await(accountMessagesDAO.find(messageId, sessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == messageId))
          assert(result1.exists(_.medium.exists(_.id == mediumId)))

          // account1 is member, so return a message
          val result2 = await(accountMessagesDAO.find(messageId, accountId1.toSessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.medium.exists(_.id == mediumId)))

          // account2 is not member, so no return
          val result3 = await(accountMessagesDAO.find(messageId, accountId2.toSessionId))
          assert(result3.isEmpty)
      }
  }

  feature("find messages") {
    scenario("should return account messages") {
      forAll(accountGen, accountGen, channelGen, message20ListGen, booleanGen) { (s, a1, g, l, ascending) =>

        // preparing
        //  session account is owner
        //  accountId1 is not a member
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))

        val accountCount = await(channelsDAO.findAccountCount(channelId))

        // create message
        val messages = l.map({ case (m, i) =>
          val mediumId = i.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
          val messageId = mediumId match {
            case Some(id) =>
              await(messagesDAO.create(channelId, id, accountCount, sessionId))
            case None =>
              await(messagesDAO.create(channelId, m.message.getOrElse(""), accountCount, sessionId))
          }
          await(accountMessagesDAO.create(channelId, messageId, sessionId))
          m.copy(id = messageId, mediumId = mediumId)
        })

        // sorting
        val createdMessages = if (ascending) {
          messages
        } else {
          messages.reverse
        }

        // should return first page
        val result1 = await(accountMessagesDAO.find(channelId, None, 0, 10, ascending, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i).id)
          assert(m.medium.map(_.id) == createdMessages(i).mediumId)
        })

        // should return next page
        val size2 = result1.size
        val result2 = await(accountMessagesDAO.find(channelId, result1.lastOption.map(_.next), 0, 10, ascending, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map({ case (m, i) =>
          assert(m.id == createdMessages(i + size2).id)
          assert(m.medium.map(_.id) == createdMessages(i + size2).mediumId)
        })

        // should not return
        val result3 = await(accountMessagesDAO.find(channelId, result2.lastOption.map(_.next), 0, 10, ascending, sessionId))
        assert(result3.size == 0)


        // should not return by no member account
        val result4 = await(accountMessagesDAO.find(channelId, None, 0, 10, ascending, accountId1.toSessionId))
        assert(result4.size == 0)

      }
    }

  }

  feature("updateUnread") {
    scenario("should update unread to true") {
      forOne(accountGen, accountGen, channelGen, messageGen) {
        (s, a1, g, m) =>

          // preparing
          //  session account is owner
          //  accountId1 is a member
          //  accountId2 is not a member
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(sessionId.toAccountId, channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val accountCount = await(channelsDAO.findAccountCount(channelId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), accountCount, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))

          // account1 read a message
          await(accountMessagesDAO.updateUnread(List(messageId), accountId1.toSessionId))

          // session account is member, so return a message
          val result1 = await(accountMessagesDAO.find(messageId, sessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == messageId))
          assert(result1.exists(_.unread))

          // account1 is member, so return a message
          val result2 = await(accountMessagesDAO.find(messageId, accountId1.toSessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.id == messageId))
          assert(result2.exists(!_.unread))

      }

    }
  }



}
