package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class ChannelsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create one to one chat channel") {
      forAll(userGen) { a =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val channelId = await(channelsDAO.create(sessionId))
        assert(await(existsChannel(channelId)))
      }
    }

    scenario("should create new channel") {
      forAll(userGen, channelNameGen, booleanGen, channelPrivacyTypeGen, channelAuthorityTypeGen) {
        (a, channelName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(usersDAO.create(a.userName)).sessionId
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
      forOne(userGen, channelNameGen, booleanGen, channelPrivacyTypeGen, channelAuthorityTypeGen) {
        (a, channelName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(usersDAO.create(a.userName)).sessionId
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
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(invitationsDAO.create(userId, channelId, sessionId))
        await(channelsDAO.delete(channelId))

        // result
        assertFutureValue(existsChannel(channelId), false)
        assertFutureValue(invitationsDAO.exists(userId, channelId), false)
      }
    }

    scenario("should delete all messages if channel deleted") {
      forOne(userGen, userGen, everyoneChannelGen, textMessageGen) { (s, a, g, m) =>
        // preparing
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(channelId, sessionId))
        await(userChannelsDAO.create(channelId, userId.sessionId))
        val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
        await(userMessagesDAO.create(channelId, messageId, sessionId))
        await(channelsDAO.delete(channelId))

        // result
        assertFutureValue(existsMessage(messageId), false)
        assertFutureValue(existsChannel(channelId), false)
        val result1 = await(userMessagesDAO.find(channelId, None, 0, 10, false, sessionId))
        assert(result1.size == 0)
        val result2 = await(userMessagesDAO.find(channelId, None, 0, 10, false, userId.sessionId))
        assert(result2.size == 0)
      }
    }

  }

  feature("exists") {
    scenario("should return a channel exist or not") {
      forOne(userGen, userGen, userGen, userGen) { (a1, a2, a3, a4) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        val channelId = await(channelsDAO.create(userId1.sessionId))
        await(blocksDAO.create(userId3, userId1.sessionId))
        await(blocksDAO.create(userId1, userId2.sessionId))
        await(blocksDAO.create(userId2, userId3.sessionId))
        assertFutureValue(channelsDAO.exists(channelId, userId1.sessionId), true)
        assertFutureValue(channelsDAO.exists(channelId, userId2.sessionId), true)
        assertFutureValue(channelsDAO.exists(channelId, userId3.sessionId), false)
        assertFutureValue(channelsDAO.exists(channelId, userId4.sessionId), true)
      }
    }

  }

  feature("find a channel") {
    scenario("should return a channel") {
      forAll(userGen, userGen, userGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        await(userChannelsDAO.create(userId2, channelId, sessionId))
        val result = await(channelsDAO.find(channelId, sessionId))
        assert(result.isDefined)
        assert(result.headOption.exists(_.id == channelId))
        assert(result.headOption.exists(_.name == g.name))
        assert(result.headOption.exists(_.invitationOnly == g.invitationOnly))
        assert(result.headOption.exists(_.privacyType == g.privacyType))
        assert(result.headOption.exists(_.authorityType == g.authorityType))
        assert(result.headOption.exists(_.userCount == 2L))
      }
    }

    scenario("should not return if user being blocked") {
      forAll(userGen, userGen, userGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        await(blocksDAO.create(userId2, sessionId))
        val result = await(channelsDAO.find(channelId, userId2.sessionId))
        assert(result.isEmpty)
      }
    }

    }

  feature("findUserCount") {
    scenario("should return channel user count") {
      forAll(userGen, userGen, userGen, channelGen) {
        (s, a1, a2, g) =>
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(channelId, sessionId))
          await(userChannelsDAO.create(userId1, channelId, sessionId))
          await(userChannelsDAO.create(userId2, channelId, sessionId))
          val result1 = await(channelsDAO.findUserCount(channelId))
          assert(result1 == 3)
          await(userChannelsDAO.delete(userId1, channelId))
          await(userChannelsDAO.delete(userId2, channelId))
          val result2 = await(channelsDAO.findUserCount(channelId))
          assert(result2 == 1)
      }
    }
  }


  feature("updateLatestMessage") (pending)

  feature("update") {
    scenario("should update a channel") {
      forAll(userGen, channelNameGen, booleanGen, channelPrivacyTypeGen, channelAuthorityTypeGen) {
        (a, channelName, invitationOnly, privacyType, authorityType) =>
          val sessionId = await(usersDAO.create(a.userName)).sessionId
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

