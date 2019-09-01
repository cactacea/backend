package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class InvitationsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a invitation") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(userId, channelId, sessionId))
        val result = await(invitationsDAO.find(userId, id))
        assert(result.isDefined)
      }
    }

    scenario("should return an exception occurs when duplication") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(invitationsDAO.create(userId, channelId, sessionId))
        assert(intercept[ServerError] {
          await(invitationsDAO.create(userId, channelId, sessionId))
        }.code == 1062)
      }
    }


  }

  feature("delete - delete a channel invitations") {
    scenario("should delete a invitation") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(userId, channelId, sessionId))
        await(invitationsDAO.delete(userId, channelId, sessionId))
        val result = await(invitationsDAO.find(userId, id))
        assert(result.isEmpty)
      }
    }
    scenario("should delete a session invitation") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(userId, channelId, sessionId))
        await(invitationsDAO.delete(id, userId.sessionId))
        val result = await(invitationsDAO.find(userId, id))
        assert(result.isEmpty)
      }
    }
  }

  feature("exists") {
    scenario("should return a channel exist or not") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        val result2 = await(invitationsDAO.exists(userId, channelId))
        assert(!result2)

        await(invitationsDAO.create(userId, channelId, sessionId))
        val result1 = await(invitationsDAO.exists(userId, channelId))
        assert(result1)
      }
    }
  }

  feature("find") {

    scenario("should return channelId userId") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsDAO.create(userId, channelId, sessionId))
        val result = await(invitationsDAO.find(userId, id))
        assert(result.isDefined)
        assert(result.exists(_._1 == channelId))
        assert(result.exists(_._2 == userId))
      }
    }

    scenario("should return received invitations") {
      forOne(userGen, users20ListGen, channelGen) { (s, l, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val creates = l.map({a =>
          val userId = await(usersDAO.create(a.userName))
          val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
          val id = await(invitationsDAO.create(sessionId.userId, channelId, userId.sessionId))
          (id, a.copy(id = userId))
        }).reverse

        val result1 = await(invitationsDAO.find(None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i)._1)
          assert(r.user.id == creates(i)._2.id)
        }

        val result2 = await(invitationsDAO.find(result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i + result1.size)._1)
          assert(r.user.id == creates(i + result1.size)._2.id)
        }

        val result3 = await(invitationsDAO.find(result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(userGen, userGen, channelGen) { (s, a, g) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val channelId = await(channelsDAO.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        val result1 = await(invitationsDAO.own(userId, channelId, sessionId))
        assert(!result1)

        await(invitationsDAO.create(userId, channelId, sessionId))
        val result2 = await(invitationsDAO.own(userId, channelId, sessionId))
        assert(result2)

        val result3 = await(invitationsDAO.own(sessionId.userId, channelId, userId.sessionId))
        assert(!result3)

      }
    }
  }

}
