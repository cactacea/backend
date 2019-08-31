package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class ChannelsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create one to one chat channel") {
      forAll(accountGen) { a =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val channelId = await(channelsDAO.create(sessionId))
        assert(await(existsChannel(channelId)))
      }
    }

    scenario("should create new channel") {
      forAll(accountGen, channelNameGen, booleanGen, channelPrivacyTypeGen, channelAuthorityTypeGen) {
        (a, channelName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsDAO.create(channelName, invitationOnly, privacyType, authorityType, sessionId))
          val result = await(findChannel(channelId))
          assert(result.flatMap(_.name) == channelName)
          assert(result.map(_.invitationOnly) == Option(invitationOnly))
          assert(result.map(_.privacyType) == Option(privacyType))
          assert(result.map(_.authorityType) == Option(authorityType))
      }
    }

  }

  feature("delete") {

    scenario("should delete a channel") {
      forOne(accountGen, channelNameGen, booleanGen, channelPrivacyTypeGen, channelAuthorityTypeGen) {
        (a, channelName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId1 = await(channelsDAO.create(channelName, invitationOnly, privacyType, authorityType, sessionId))
          val channelId2 = await(channelsDAO.create(channelName, invitationOnly, privacyType, authorityType, sessionId))
          val channelId3 = await(channelsDAO.create(channelName, invitationOnly, privacyType, authorityType, sessionId))
          val channelId4 = await(channelsDAO.create(channelName, invitationOnly, privacyType, authorityType, sessionId))
          await(channelsDAO.delete(channelId1))
          await(channelsDAO.delete(channelId2))
          await(channelsDAO.delete(channelId3))
          await(channelsDAO.delete(channelId4))
          assert(!await(existsChannel(channelId1)))
          assert(!await(existsChannel(channelId2)))
          assert(!await(existsChannel(channelId3)))
          assert(!await(existsChannel(channelId4)))
      }
    }

    scenario("should delete all invitations if channel deleted") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a, g) =>
        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(invitationsDAO.create(accountId, channelId, sessionId))
        await(channelsDAO.delete(channelId))

        // result
        assertFutureValue(existsChannel(channelId), false)
        assertFutureValue(invitationsDAO.exists(accountId, channelId), false)
      }
    }

    scenario("should delete all messages if channel deleted") {
      forOne(accountGen, accountGen, everyoneChannelGen, textMessageGen) { (s, a, g, m) =>
        // preparing
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(channelId, sessionId))
        await(accountChannelsDAO.create(channelId, accountId.toSessionId))
        val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
        await(accountMessagesDAO.create(channelId, messageId, sessionId))
        await(channelsDAO.delete(channelId))

        // result
        assertFutureValue(existsMessage(messageId), false)
        assertFutureValue(existsChannel(channelId), false)
        val result1 = await(accountMessagesDAO.find(channelId, None, 0, 10, false, sessionId))
        assert(result1.size == 0)
        val result2 = await(accountMessagesDAO.find(channelId, None, 0, 10, false, accountId.toSessionId))
        assert(result2.size == 0)
      }
    }

  }

  feature("exists") {
    scenario("should return a channel exist or not") {
      forOne(accountGen, accountGen, accountGen, accountGen) { (a1, a2, a3, a4) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        val channelId = await(channelsDAO.create(accountId1.toSessionId))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        await(blocksDAO.create(accountId2, accountId3.toSessionId))
        assertFutureValue(channelsDAO.exists(channelId, accountId1.toSessionId), true)
        assertFutureValue(channelsDAO.exists(channelId, accountId2.toSessionId), true)
        assertFutureValue(channelsDAO.exists(channelId, accountId3.toSessionId), false)
        assertFutureValue(channelsDAO.exists(channelId, accountId4.toSessionId), true)
      }
    }

  }

  feature("find a channel") {
    scenario("should return a channel") {
      forAll(accountGen, accountGen, accountGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        await(accountChannelsDAO.create(accountId2, channelId, sessionId))
        val result = await(channelsDAO.find(channelId, sessionId))
        assert(result.isDefined)
        assert(result.headOption.exists(_.id == channelId))
        assert(result.headOption.exists(_.name == g.name))
        assert(result.headOption.exists(_.invitationOnly == g.invitationOnly))
        assert(result.headOption.exists(_.privacyType == g.privacyType))
        assert(result.headOption.exists(_.authorityType == g.authorityType))
        assert(result.headOption.exists(_.accountCount == 2L))
      }
    }

    scenario("should not return if account being blocked") {
      forAll(accountGen, accountGen, accountGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        await(blocksDAO.create(accountId2, sessionId))
        val result = await(channelsDAO.find(channelId, accountId2.toSessionId))
        assert(result.isEmpty)
      }
    }

    }

  feature("findAccountCount") {
    scenario("should return channel account count") {
      forAll(accountGen, accountGen, accountGen, channelGen) {
        (s, a1, a2, g) =>
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(channelId, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          await(accountChannelsDAO.create(accountId2, channelId, sessionId))
          val result1 = await(channelsDAO.findAccountCount(channelId))
          assert(result1 == 3)
          await(accountChannelsDAO.delete(accountId1, channelId))
          await(accountChannelsDAO.delete(accountId2, channelId))
          val result2 = await(channelsDAO.findAccountCount(channelId))
          assert(result2 == 1)
      }
    }
  }


  feature("updateLatestMessage") (pending)

  feature("update") {
    scenario("should update a channel") {
      forAll(accountGen, channelNameGen, booleanGen, channelPrivacyTypeGen, channelAuthorityTypeGen) {
        (a, channelName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
          val channelId = await(channelsDAO.create(sessionId))
          await(channelsDAO.update(channelId, channelName, invitationOnly, privacyType, authorityType, sessionId))
          val result = await(findChannel(channelId))
          assert(result.flatMap(_.name) == channelName)
          assert(result.map(_.invitationOnly) == Option(invitationOnly))
          assert(result.map(_.privacyType) == Option(privacyType))
          assert(result.map(_.authorityType) == Option(authorityType))
      }
    }
  }




}

