package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class ChannelAccountsDAOSpec extends DAOSpec {

  feature("find") {
    scenario("should return channels") {
      forOne(accountGen, accounts20ListGen, channelGen) { (s, l, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val accountIds = l.map({ a =>
          val accountId = await(accountsDAO.create(a.accountName))
          await(accountChannelsDAO.create(accountId, channelId, sessionId))
          accountId
        }).reverse

        // page1 found
        val result1 = await(channelAccountsDAO.find(channelId, None, 0, 10))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (a, i) =>
            assert(a.id == accountIds(i))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(channelAccountsDAO.find(channelId, result1.lastOption.map(_.next), 0, 10))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (a, i) =>
          assert(a.id == accountIds(i + size1))
        }

        // page3 not found
        val result3 = await(channelAccountsDAO.find(channelId, result2.lastOption.map(_.next), 0, 10))
        assert(result3.size == 0)


      }
    }

  }

  feature("exists") {
    scenario("should return join or not") {
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
        assertFutureValue(channelAccountsDAO.exists(channelId, accountId1), true)
        assertFutureValue(channelAccountsDAO.exists(channelId, accountId2), false)
      }
    }
  }

}

