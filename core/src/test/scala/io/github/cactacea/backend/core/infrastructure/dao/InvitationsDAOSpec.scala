package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class InvitationsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a invitation") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(accountId, channelId, sessionId))
        val result = await(invitationsDAO.find(accountId, id))
        assert(result.isDefined)
      }
    }

    scenario("should return an exception occurs when duplication") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(invitationsDAO.create(accountId, channelId, sessionId))
        assert(intercept[ServerError] {
          await(invitationsDAO.create(accountId, channelId, sessionId))
        }.code == 1062)
      }
    }


  }

  feature("delete - delete a channel invitations") {
    scenario("should delete a invitation") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(accountId, channelId, sessionId))
        await(invitationsDAO.delete(accountId, channelId, sessionId))
        val result = await(invitationsDAO.find(accountId, id))
        assert(result.isEmpty)
      }
    }
    scenario("should delete a session invitation") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(accountId, channelId, sessionId))
        await(invitationsDAO.delete(id, accountId.toSessionId))
        val result = await(invitationsDAO.find(accountId, id))
        assert(result.isEmpty)
      }
    }
  }

  feature("exists") {
    scenario("should return a channel exist or not") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        val result2 = await(invitationsDAO.exists(accountId, channelId))
        assert(!result2)

        await(invitationsDAO.create(accountId, channelId, sessionId))
        val result1 = await(invitationsDAO.exists(accountId, channelId))
        assert(result1)
      }
    }
  }

  feature("find") {

    scenario("should return channelId accountId") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(accountId, channelId, sessionId))
        val result = await(invitationsDAO.find(accountId, id))
        assert(result.isDefined)
        assert(result.exists(_._1 == channelId))
        assert(result.exists(_._2 == accountId))
      }
    }

    scenario("should return received invitations") {
      forOne(accountGen, accounts20ListGen, channelGen) { (s, l, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val creates = l.map({a =>
          val accountId = await(accountsDAO.create(a.accountName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val id = await(invitationsDAO.create(sessionId.toAccountId, channelId, accountId.toSessionId))
          (id, a.copy(id = accountId))
        }).reverse

        val result1 = await(invitationsDAO.find(None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i)._1)
          assert(r.account.id == creates(i)._2.id)
        }

        val result2 = await(invitationsDAO.find(result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i + result1.size)._1)
          assert(r.account.id == creates(i + result1.size)._2.id)
        }

        val result3 = await(invitationsDAO.find(result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(accountGen, accountGen, channelGen) { (s, a, g) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        val result1 = await(invitationsDAO.own(accountId, channelId, sessionId))
        assert(!result1)

        await(invitationsDAO.create(accountId, channelId, sessionId))
        val result2 = await(invitationsDAO.own(accountId, channelId, sessionId))
        assert(result2)

        val result3 = await(invitationsDAO.own(sessionId.toAccountId, channelId, accountId.toSessionId))
        assert(!result3)

      }
    }
  }

}
