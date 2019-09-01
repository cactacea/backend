package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class UserChannelsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should join an user to a channel") {
      forOne(userGen, userGen, userGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        await(userChannelsDAO.create(userId2, channelId, sessionId))
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId2), true)
        val result = await(channelsDAO.find(channelId, sessionId))
        assert(result.headOption.map(_.userCount) == Option(2L))
      }
    }

    scenario("should return an exception occurs if duplication") {
      forOne(userGen, userGen, channelGen) { (s, a1, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        assert(intercept[ServerError] {
          await(userChannelsDAO.create(userId1, channelId, sessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should leave an user from a channel") {
      forAll(userGen, userGen, userGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        await(userChannelsDAO.create(userId2, channelId, sessionId))
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId2), true)
        val result1 = await(channelsDAO.find(channelId, sessionId))
        assert(result1.headOption.map(_.userCount) == Option(2L))

        await(userChannelsDAO.delete(userId1, channelId))
        await(userChannelsDAO.delete(userId2, channelId))
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), false)
        assertFutureValue(userChannelsDAO.exists(channelId, userId2), false)
        val result2 = await(channelsDAO.find(channelId, sessionId))
        assert(result2.headOption.map(_.userCount) == Option(0L))

      }
    }
  }

  feature("exists") {
    scenario("should return joined or not") {
      forOne(userGen, userGen, userGen, channelGen) { (s, a1, a2, g) =>
        // preparing
        //  session user creates a channel
        //  user1 joins to the channel
        //  user2 dose not join to the channel
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))

        // return user1 joined
        // return user2 not joined
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId2), false)
      }
    }
  }

  feature("updateUnreadCount") {
    scenario("should increase unread message count") {
      forOne(userGen, userGen, channelGen) { (s, a1, g) =>
        // preparing
        //  session user creates a channel
        //  user1 joins to the channel
        //  session user hides the channel
        //  session user update unread count twice
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        await(userChannelsDAO.updateHidden(channelId, true, sessionId))
        await(userChannelsDAO.updateUnreadCount(channelId))
        await(userChannelsDAO.updateUnreadCount(channelId))

        // return the channel not hidden
        // return the channel unread count is 2
        val result = findUserChannel(channelId, userId1)
        assert(result.exists(!_.hidden))
        assert(result.map(_.unreadCount) == Option(2L))
      }
    }
  }

  feature("updateHidden") {
    scenario("should show and hide channel") {
      forOne(userGen, userGen, userGen, channelGen, channelGen) { (s, a1, a2, g1, g2) =>
        // preparing
        //   session user creates two channel2
        //   user1 joins to the channel 1
        //   user2 joins to the channel 2
        //   user1 hides the channel 1
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val channelId1 = await(channelsDAO.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
        val channelId2 = await(channelsDAO.create(g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId1, sessionId))
        await(userChannelsDAO.create(userId2, channelId2, sessionId))
        await(userChannelsDAO.updateHidden(channelId1, true, userId1.sessionId))

        // return channel1 is hidden
        // return channel2 is not hidden
        assertFutureValue(userChannelsDAO.isHidden(channelId1, userId1.sessionId), Option(true))
        assertFutureValue(userChannelsDAO.isHidden(channelId2, userId2.sessionId), Option(false))

        // preparing
        //   user1 shows the channel1
        //   user2 hides the channel2
        await(userChannelsDAO.updateHidden(channelId1, false, userId1.sessionId))
        await(userChannelsDAO.updateHidden(channelId2, true, userId2.sessionId))

        // return channel1 is hidden
        // return channel2 is not hidden
        assertFutureValue(userChannelsDAO.isHidden(channelId1, userId1.sessionId), Option(false))
        assertFutureValue(userChannelsDAO.isHidden(channelId2, userId2.sessionId), Option(true))
      }
    }
  }

  feature("findByUserId") {
    scenario("should return a channel") {
      forOne(userGen, userGen) { (s, a1 ) =>

        // preparing
        //   session create a channel
        //   user1 join the channel
        //   session create a message
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channelId = await(channelsDAO.create(sessionId))
        await(userChannelsDAO.create(channelId, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))

        // result
        val result = await(userChannelsDAO.findByUserId(userId1, sessionId))
        assert(result.isDefined)
        assert(result.exists(_.invitationOnly))
        assert(result.exists(_.userCount == 2))
        assert(result.exists(_.authorityType == ChannelAuthorityType.member))
        assert(result.exists(_.id == channelId))
        assert(result.exists(_.message.isEmpty))
        assert(result.exists(_.privacyType == ChannelPrivacyType.everyone))
      }

    }
    scenario("should return a channel with latest message") {
      forOne(userGen, userGen, textMessageGen) { (s, a1, m) =>

        // preparing
        //   session create a channel
        //   user1 join the channel
        //   session create a message
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channelId = await(channelsDAO.create(sessionId))
        await(userChannelsDAO.create(channelId, sessionId))
        await(userChannelsDAO.create(userId1, channelId, sessionId))
        val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
        await(userMessagesDAO.create(channelId, messageId, sessionId))

        // result
        val result = await(userChannelsDAO.findByUserId(userId1, sessionId))
        assert(result.isDefined)
        assert(result.exists(_.invitationOnly))
        assert(result.exists(_.userCount == 2))
        assert(result.exists(_.authorityType == ChannelAuthorityType.member))
        assert(result.exists(_.id == channelId))
        assert(result.exists(_.message.exists(_.message == m.message)))
        assert(result.exists(_.message.exists(_.messageType == m.messageType)))
        assert(result.exists(_.message.exists(_.userCount == 2)))
        assert(result.exists(_.message.exists(_.user.id == sessionId.userId)))
        assert(result.exists(_.message.exists(_.readUserCount == 0)))
        assert(result.exists(_.message.exists(!_.rejected)))
        assert(result.exists(_.name.isEmpty))
        assert(result.exists(_.privacyType == ChannelPrivacyType.everyone))
      }
    }

    scenario("should return a channel with latest message and medium") {
      forOne(userGen, userGen, mediumGen) {
        (s, a1, i) =>

          // preparing
          //   session create a channel
          //   user1 join the channel
          //   session create a message
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val channelId = await(channelsDAO.create(sessionId))
          await(userChannelsDAO.create(channelId, sessionId))
          await(userChannelsDAO.create(userId1, channelId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val userCount = await(channelsDAO.findUserCount(channelId))
          val messageId = await(messagesDAO.create(channelId, mediumId, userCount, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))

          // result
          val result = await(userChannelsDAO.findByUserId(userId1, sessionId))
          assert(result.isDefined)
          assert(result.exists(_.invitationOnly))
          assert(result.exists(_.userCount == 2))
          assert(result.exists(_.authorityType == ChannelAuthorityType.member))
          assert(result.exists(_.id == channelId))
          assert(result.exists(_.message.exists(_.message.isEmpty)))
          assert(result.exists(_.message.exists(_.messageType == MessageType.medium)))
          assert(result.exists(_.message.exists(_.userCount == 2)))
          assert(result.exists(_.message.exists(_.user.id == sessionId.userId)))
          assert(result.exists(_.message.exists(_.readUserCount == 0)))
          assert(result.exists(_.message.exists(!_.rejected)))
          assert(result.exists(_.message.exists(_.medium.exists(_.id == mediumId))))
          assert(result.exists(_.message.exists(_.medium.exists(_.uri == i.uri))))
          assert(result.exists(_.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl))))
          assert(result.exists(_.message.exists(_.medium.exists(_.mediumType == i.mediumType))))
          assert(result.exists(_.message.exists(_.medium.exists(_.width == i.width))))
          assert(result.exists(_.message.exists(_.medium.exists(_.height == i.height))))
          assert(result.exists(_.message.exists(_.medium.exists(_.size == i.size))))
          assert(result.exists(_.name.isEmpty))
          assert(result.exists(_.privacyType == ChannelPrivacyType.everyone))

      }
    }

  }



  feature("find") {
    scenario("should return channels") {
      forOne(userGen, userGen, messageGen, channel20ListGen) { (s, a1, m, g) =>

        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channels = g.map({g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(userId1, channelId, sessionId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(userChannelsDAO.find(userId1, None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i).id)
          assert(r.message.exists(_.channelId == channels(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.userCount == 2))
          assert(r.message.exists(_.readUserCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(userChannelsDAO.find(userId1, result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i + size1).id)
          assert(r.message.exists(_.channelId == channels(i + size1).id))
          assert(r.message.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.userCount == 2))
          assert(r.message.exists(_.readUserCount == 0))
        }

        // page3 not found
        val result3 = await(userChannelsDAO.find(userId1, result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }

    }

    scenario("should return latest message and medium") {

      forOne(userGen, userGen, mediumGen, channel20ListGen) { (s, a1, i, g) =>

        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channels = g.map({g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(userId1, channelId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val messageId = await(messagesDAO.create(channelId, mediumId, 2, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(userChannelsDAO.find(userId1, None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, index) =>
          assert(r.id == channels(index).id)
          assert(r.message.exists(_.channelId == channels(index).id))
          assert(r.message.exists(_.message.isEmpty))
          assert(r.message.exists(_.messageType == MessageType.medium))
          assert(r.message.exists(_.userCount == 2))
          assert(r.message.exists(_.readUserCount == 0))
          assert(r.message.exists(_.medium.exists(_.uri == i.uri)))
          assert(r.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl)))
          assert(r.message.exists(_.medium.exists(_.mediumType == i.mediumType)))
          assert(r.message.exists(_.medium.exists(_.width == i.width)))
          assert(r.message.exists(_.medium.exists(_.height == i.height)))
          assert(r.message.exists(_.medium.exists(_.size == i.size)))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(userChannelsDAO.find(userId1, result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, index) =>
          assert(r.id == channels(index + size1).id)
          assert(r.message.exists(_.channelId == channels(index + size1).id))
          assert(r.message.exists(_.message.isEmpty))
          assert(r.message.exists(_.messageType == MessageType.medium))
          assert(r.message.exists(_.userCount == 2))
          assert(r.message.exists(_.readUserCount == 0))
          assert(r.message.exists(_.medium.exists(_.uri == i.uri)))
          assert(r.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl)))
          assert(r.message.exists(_.medium.exists(_.mediumType == i.mediumType)))
          assert(r.message.exists(_.medium.exists(_.width == i.width)))
          assert(r.message.exists(_.medium.exists(_.height == i.height)))
          assert(r.message.exists(_.medium.exists(_.size == i.size)))
        }

        // page3 not found
        val result3 = await(userChannelsDAO.find(userId1, result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }

    }

    scenario("should filter hidden or not") {

      forOne(userGen, userGen, messageGen, channel20ListGen) { (s, a1, m, g) =>

        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channels = g.map({g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(userChannelsDAO.create(userId1, channelId, sessionId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
          await(userMessagesDAO.create(channelId, messageId, sessionId))
          g.copy(id = channelId)
        }).reverse

        channels.foreach({ g =>
          await(userChannelsDAO.updateHidden(g.id, false, userId1.sessionId))
        })

        val result1 = await(userChannelsDAO.find(userId1, None, 0, 10, true, sessionId))
        assert(result1.size == 0)

        channels.foreach({ g =>
          await(userChannelsDAO.updateHidden(g.id, true, userId1.sessionId))
        })

        val result2 = await(userChannelsDAO.find(userId1, None, 0, 10, false, sessionId))
        assert(result2.size == 0)

      }

    }
  }

  feature("isHidden") {
    scenario("should return hidden or not") {
      forAll(userGen, userGen, channelGen, channelGen, channelGen) { (s, a1, g1, g2, g3) =>
        // preparing
        //   session user creates two channel2
        //   user1 joins to the channel 1
        //   user1 joins to the channel 2
        //   user1 dose not join to the channel 3
        //   user1 hides the channel 1
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val channelId1 = await(channelsDAO.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
        val channelId2 = await(channelsDAO.create(g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
        val channelId3 = await(channelsDAO.create(g3.name, g3.invitationOnly, g3.privacyType, g3.authorityType, sessionId))
        await(userChannelsDAO.create(userId1, channelId1, sessionId))
        await(userChannelsDAO.create(userId1, channelId2, sessionId))
        await(userChannelsDAO.updateHidden(channelId1, true, userId1.sessionId))

        // return channel1 is hidden
        // return channel2 is not hidden
        // return channel3 is Unknown
        assertFutureValue(userChannelsDAO.isHidden(channelId1, userId1.sessionId), Option(true))
        assertFutureValue(userChannelsDAO.isHidden(channelId2, userId1.sessionId), Option(false))
        assertFutureValue(userChannelsDAO.isHidden(channelId3, userId1.sessionId), None)
      }
    }
  }



}

