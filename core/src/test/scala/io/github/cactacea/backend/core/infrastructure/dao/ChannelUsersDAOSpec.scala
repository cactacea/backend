package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class ChannelUsersDAOSpec extends DAOSpec {

  feature("find") {
    scenario("should return channels") {
      forOne(userGen, users20ListGen, channelGen) { (s, l, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val userIds = l.map({ a =>
          val userId = await(usersDAO.create(a.userName))
          await(userChannelsDAO.create(userId, channelId, sessionId))
          userId
        }).reverse

        // page1 found
        val result1 = await(channelUsersDAO.find(channelId, None, 0, 10))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (a, i) =>
            assert(a.id == userIds(i))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(channelUsersDAO.find(channelId, result1.lastOption.map(_.next), 0, 10))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (a, i) =>
          assert(a.id == userIds(i + size1))
        }

        // page3 not found
        val result3 = await(channelUsersDAO.find(channelId, result2.lastOption.map(_.next), 0, 10))
        assert(result3.size == 0)


      }
    }

  }

  feature("exists") {
    scenario("should return join or not") {
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
        assertFutureValue(channelUsersDAO.exists(channelId, userId1), true)
        assertFutureValue(channelUsersDAO.exists(channelId, userId2), false)
      }
    }
  }

}

