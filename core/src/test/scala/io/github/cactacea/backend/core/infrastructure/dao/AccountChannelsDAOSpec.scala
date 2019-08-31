package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class AccountChannelsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should join an account to a channel") {
      forOne(accountGen, accountGen, accountGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        await(accountChannelsDAO.create(accountId2, channelId, sessionId))
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId2), true)
        val result = await(channelsDAO.find(channelId, sessionId))
        assert(result.headOption.map(_.accountCount) == Option(2L))
      }
    }

    scenario("should return an exception occurs if duplication") {
      forOne(accountGen, accountGen, channelGen) { (s, a1, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        assert(intercept[ServerError] {
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should leave an account from a channel") {
      forAll(accountGen, accountGen, accountGen, channelGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        await(accountChannelsDAO.create(accountId2, channelId, sessionId))
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId2), true)
        val result1 = await(channelsDAO.find(channelId, sessionId))
        assert(result1.headOption.map(_.accountCount) == Option(2L))

        await(accountChannelsDAO.delete(accountId1, channelId))
        await(accountChannelsDAO.delete(accountId2, channelId))
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), false)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId2), false)
        val result2 = await(channelsDAO.find(channelId, sessionId))
        assert(result2.headOption.map(_.accountCount) == Option(0L))

      }
    }
  }

  feature("exists") {
    scenario("should return joined or not") {
      forOne(accountGen, accountGen, accountGen, channelGen) { (s, a1, a2, g) =>
        // preparing
        //  session account creates a channel
        //  account1 joins to the channel
        //  account2 dose not join to the channel
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))

        // return account1 joined
        // return account2 not joined
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId2), false)
      }
    }
  }

  feature("updateUnreadCount") {
    scenario("should increase unread message count") {
      forOne(accountGen, accountGen, channelGen) { (s, a1, g) =>
        // preparing
        //  session account creates a channel
        //  account1 joins to the channel
        //  session account hides the channel
        //  session account update unread count twice
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        await(accountChannelsDAO.updateHidden(channelId, true, sessionId))
        await(accountChannelsDAO.updateUnreadCount(channelId))
        await(accountChannelsDAO.updateUnreadCount(channelId))

        // return the channel not hidden
        // return the channel unread count is 2
        val result = findAccountChannel(channelId, accountId1)
        assert(result.exists(!_.hidden))
        assert(result.map(_.unreadCount) == Option(2L))
      }
    }
  }

  feature("updateHidden") {
    scenario("should show and hide channel") {
      forOne(accountGen, accountGen, accountGen, channelGen, channelGen) { (s, a1, a2, g1, g2) =>
        // preparing
        //   session account creates two channel2
        //   account1 joins to the channel 1
        //   account2 joins to the channel 2
        //   account1 hides the channel 1
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val channelId1 = await(channelsDAO.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
        val channelId2 = await(channelsDAO.create(g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId1, sessionId))
        await(accountChannelsDAO.create(accountId2, channelId2, sessionId))
        await(accountChannelsDAO.updateHidden(channelId1, true, accountId1.toSessionId))

        // return channel1 is hidden
        // return channel2 is not hidden
        assertFutureValue(accountChannelsDAO.isHidden(channelId1, accountId1.toSessionId), Option(true))
        assertFutureValue(accountChannelsDAO.isHidden(channelId2, accountId2.toSessionId), Option(false))

        // preparing
        //   account1 shows the channel1
        //   account2 hides the channel2
        await(accountChannelsDAO.updateHidden(channelId1, false, accountId1.toSessionId))
        await(accountChannelsDAO.updateHidden(channelId2, true, accountId2.toSessionId))

        // return channel1 is hidden
        // return channel2 is not hidden
        assertFutureValue(accountChannelsDAO.isHidden(channelId1, accountId1.toSessionId), Option(false))
        assertFutureValue(accountChannelsDAO.isHidden(channelId2, accountId2.toSessionId), Option(true))
      }
    }
  }

  feature("findByAccountId") {
    scenario("should return a channel") {
      forOne(accountGen, accountGen) { (s, a1 ) =>

        // preparing
        //   session create a channel
        //   account1 join the channel
        //   session create a message
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channelId = await(channelsDAO.create(sessionId))
        await(accountChannelsDAO.create(channelId, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))

        // result
        val result = await(accountChannelsDAO.findByAccountId(accountId1, sessionId))
        assert(result.isDefined)
        assert(result.exists(_.invitationOnly))
        assert(result.exists(_.accountCount == 2))
        assert(result.exists(_.authorityType == ChannelAuthorityType.member))
        assert(result.exists(_.id == channelId))
        assert(result.exists(_.message.isEmpty))
        assert(result.exists(_.privacyType == ChannelPrivacyType.everyone))
      }

    }
    scenario("should return a channel with latest message") {
      forOne(accountGen, accountGen, textMessageGen) { (s, a1, m) =>

        // preparing
        //   session create a channel
        //   account1 join the channel
        //   session create a message
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channelId = await(channelsDAO.create(sessionId))
        await(accountChannelsDAO.create(channelId, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId, sessionId))
        val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
        await(accountMessagesDAO.create(channelId, messageId, sessionId))

        // result
        val result = await(accountChannelsDAO.findByAccountId(accountId1, sessionId))
        assert(result.isDefined)
        assert(result.exists(_.invitationOnly))
        assert(result.exists(_.accountCount == 2))
        assert(result.exists(_.authorityType == ChannelAuthorityType.member))
        assert(result.exists(_.id == channelId))
        assert(result.exists(_.message.exists(_.message == m.message)))
        assert(result.exists(_.message.exists(_.messageType == m.messageType)))
        assert(result.exists(_.message.exists(_.accountCount == 2)))
        assert(result.exists(_.message.exists(_.account.id == sessionId.toAccountId)))
        assert(result.exists(_.message.exists(_.readAccountCount == 0)))
        assert(result.exists(_.message.exists(!_.rejected)))
        assert(result.exists(_.name.isEmpty))
        assert(result.exists(_.privacyType == ChannelPrivacyType.everyone))
      }
    }

    scenario("should return a channel with latest message and medium") {
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
          await(accountMessagesDAO.create(channelId, messageId, sessionId))

          // result
          val result = await(accountChannelsDAO.findByAccountId(accountId1, sessionId))
          assert(result.isDefined)
          assert(result.exists(_.invitationOnly))
          assert(result.exists(_.accountCount == 2))
          assert(result.exists(_.authorityType == ChannelAuthorityType.member))
          assert(result.exists(_.id == channelId))
          assert(result.exists(_.message.exists(_.message.isEmpty)))
          assert(result.exists(_.message.exists(_.messageType == MessageType.medium)))
          assert(result.exists(_.message.exists(_.accountCount == 2)))
          assert(result.exists(_.message.exists(_.account.id == sessionId.toAccountId)))
          assert(result.exists(_.message.exists(_.readAccountCount == 0)))
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
      forOne(accountGen, accountGen, messageGen, channel20ListGen) { (s, a1, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channels = g.map({g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(accountChannelsDAO.find(accountId1, None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i).id)
          assert(r.message.exists(_.channelId == channels(i).id))
          assert(r.message.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountChannelsDAO.find(accountId1, result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == channels(i + size1).id)
          assert(r.message.exists(_.channelId == channels(i + size1).id))
          assert(r.message.exists(_.message.getOrElse("") == m.message.getOrElse("")))
          assert(r.message.exists(_.messageType == MessageType.text))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
        }

        // page3 not found
        val result3 = await(accountChannelsDAO.find(accountId1, result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }

    }

    scenario("should return latest message and medium") {

      forOne(accountGen, accountGen, mediumGen, channel20ListGen) { (s, a1, i, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channels = g.map({g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val mediumId = await(mediumsDAO.create(i.key, i.uri, i.thumbnailUrl, i.mediumType, i.width, i.height, i.size, sessionId))
          val messageId = await(messagesDAO.create(channelId, mediumId, 2, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))
          g.copy(id = channelId)
        }).reverse

        // page1 found
        val result1 = await(accountChannelsDAO.find(accountId1, None, 0, 10, false, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, index) =>
          assert(r.id == channels(index).id)
          assert(r.message.exists(_.channelId == channels(index).id))
          assert(r.message.exists(_.message.isEmpty))
          assert(r.message.exists(_.messageType == MessageType.medium))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
          assert(r.message.exists(_.medium.exists(_.uri == i.uri)))
          assert(r.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl)))
          assert(r.message.exists(_.medium.exists(_.mediumType == i.mediumType)))
          assert(r.message.exists(_.medium.exists(_.width == i.width)))
          assert(r.message.exists(_.medium.exists(_.height == i.height)))
          assert(r.message.exists(_.medium.exists(_.size == i.size)))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(accountChannelsDAO.find(accountId1, result1.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, index) =>
          assert(r.id == channels(index + size1).id)
          assert(r.message.exists(_.channelId == channels(index + size1).id))
          assert(r.message.exists(_.message.isEmpty))
          assert(r.message.exists(_.messageType == MessageType.medium))
          assert(r.message.exists(_.accountCount == 2))
          assert(r.message.exists(_.readAccountCount == 0))
          assert(r.message.exists(_.medium.exists(_.uri == i.uri)))
          assert(r.message.exists(_.medium.exists(_.thumbnailUrl == i.thumbnailUrl)))
          assert(r.message.exists(_.medium.exists(_.mediumType == i.mediumType)))
          assert(r.message.exists(_.medium.exists(_.width == i.width)))
          assert(r.message.exists(_.medium.exists(_.height == i.height)))
          assert(r.message.exists(_.medium.exists(_.size == i.size)))
        }

        // page3 not found
        val result3 = await(accountChannelsDAO.find(accountId1, result2.lastOption.flatMap(_.next), 0, 10, false, sessionId))
        assert(result3.size == 0)

      }

    }

    scenario("should filter hidden or not") {

      forOne(accountGen, accountGen, messageGen, channel20ListGen) { (s, a1, m, g) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channels = g.map({g =>
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          await(accountChannelsDAO.create(accountId1, channelId, sessionId))
          val messageId = await(messagesDAO.create(channelId, m.message.getOrElse(""), 2, sessionId))
          await(accountMessagesDAO.create(channelId, messageId, sessionId))
          g.copy(id = channelId)
        }).reverse

        channels.foreach({ g =>
          await(accountChannelsDAO.updateHidden(g.id, false, accountId1.toSessionId))
        })

        val result1 = await(accountChannelsDAO.find(accountId1, None, 0, 10, true, sessionId))
        assert(result1.size == 0)

        channels.foreach({ g =>
          await(accountChannelsDAO.updateHidden(g.id, true, accountId1.toSessionId))
        })

        val result2 = await(accountChannelsDAO.find(accountId1, None, 0, 10, false, sessionId))
        assert(result2.size == 0)

      }

    }
  }

  feature("isHidden") {
    scenario("should return hidden or not") {
      forAll(accountGen, accountGen, channelGen, channelGen, channelGen) { (s, a1, g1, g2, g3) =>
        // preparing
        //   session account creates two channel2
        //   account1 joins to the channel 1
        //   account1 joins to the channel 2
        //   account1 dose not join to the channel 3
        //   account1 hides the channel 1
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val channelId1 = await(channelsDAO.create(g1.name, g1.invitationOnly, g1.privacyType, g1.authorityType, sessionId))
        val channelId2 = await(channelsDAO.create(g2.name, g2.invitationOnly, g2.privacyType, g2.authorityType, sessionId))
        val channelId3 = await(channelsDAO.create(g3.name, g3.invitationOnly, g3.privacyType, g3.authorityType, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId1, sessionId))
        await(accountChannelsDAO.create(accountId1, channelId2, sessionId))
        await(accountChannelsDAO.updateHidden(channelId1, true, accountId1.toSessionId))

        // return channel1 is hidden
        // return channel2 is not hidden
        // return channel3 is Unknown
        assertFutureValue(accountChannelsDAO.isHidden(channelId1, accountId1.toSessionId), Option(true))
        assertFutureValue(accountChannelsDAO.isHidden(channelId2, accountId1.toSessionId), Option(false))
        assertFutureValue(accountChannelsDAO.isHidden(channelId3, accountId1.toSessionId), None)
      }
    }
  }



}

