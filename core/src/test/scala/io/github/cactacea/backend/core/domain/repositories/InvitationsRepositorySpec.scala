package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId, InvitationId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class InvitationsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a invitation") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsRepository.create(userId, channelId, sessionId))
        val result = await(invitationsDAO.find(userId, id))
        assert(result.isDefined)
      }
    }

    scenario("should return exception if already requested") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(invitationsRepository.create(userId, channelId, sessionId))
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId, channelId, sessionId))
        }.error == UserAlreadyInvited)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen, everyoneChannelGen) { (s, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(sessionId.userId, channelId, sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen, everyoneChannelGen) { (s, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(UserId(0), channelId, sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if channel not exist") {
      forOne(userGen, userGen) { (s, a) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId, ChannelId(0), sessionId))
        }.error == ChannelNotFound)

      }
    }

    scenario("should create a invitation if friend") {
      forOne(userGen, userGen, friendChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val requestId = await(friendRequestsRepository.create(userId, sessionId))
        await(friendRequestsRepository.accept(requestId, userId.sessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId, channelId, sessionId))
        assert(await(invitationsDAO.find(userId, invitationId)).isDefined)
        await(invitationsRepository.accept(invitationId, userId.sessionId))
        assertFutureValue(userChannelsDAO.exists(channelId, userId), true)
      }
    }

    scenario("should create a invitation if follower") {
      forOne(userGen, userGen, followerChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        await(followsRepository.create(sessionId.userId, userId.sessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId, channelId, sessionId))
        assert(await(invitationsDAO.find(userId, invitationId)).isDefined)
        await(invitationsRepository.accept(invitationId, userId.sessionId))
        assertFutureValue(userChannelsDAO.exists(channelId, userId), true)
      }
    }

    scenario("should create a invitation if follow") {
      forOne(userGen, userGen, followChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        await(followsRepository.create(userId, sessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId, channelId, sessionId))
        assert(await(invitationsDAO.find(userId, invitationId)).isDefined)
        await(invitationsRepository.accept(invitationId, userId.sessionId))
        assertFutureValue(userChannelsDAO.exists(channelId, userId), true)
      }
    }



    scenario("should return exception if invite not friend user") {
      forOne(userGen, userGen, friendChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should return exception if invite not follower user") {
      forOne(userGen, userGen, followerChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should return exception if invite not follow user") {
      forOne(userGen, userGen, followChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should return exception if member authority not exist") {
      forOne(userGen, userGen, userGen, memberChannelGen) { (s, a1, a2, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId1, channelId, userId2.sessionId))
        }.error == UserNotJoined)
      }
    }

    scenario("should return exception if organizer authority not exist") {
      forOne(userGen, userGen, userGen, organizerChannelGen) { (s, a1, a2, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId2, channelId, sessionId))
        await(invitationsRepository.accept(invitationId, userId2.sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId1, channelId, userId2.sessionId))
        }.error == AuthorityNotFound)
      }
    }


    scenario("should return exception if channel is direct message") {
      forOne(userGen, userGen, userGen) { (s, a1, a2) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(userChannelsRepository.findOrCreate(userId1, sessionId)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(userId2, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }


  }


  feature("delete") {

    scenario("should delete a invitation") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId, channelId, sessionId))
        await(invitationsRepository.delete(userId, channelId, sessionId))
        val result = await(invitationsDAO.find(userId, invitationId))
        assert(result.isEmpty)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen, everyoneChannelGen) { (s, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.delete(sessionId.userId, channelId, sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen, everyoneChannelGen) { (s, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.delete(UserId(0), channelId, sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if channel not exist") {
      forOne(userGen, userGen) { (s, a) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.delete(userId, ChannelId(0), sessionId))
        }.error == ChannelNotFound)

      }
    }
  }

  feature("find") {
    scenario("should return received invitations") {
      forOne(userGen, user20SeqGen, everyoneChannelGen) { (s, l, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val creates = l.map({a =>
          val userId = await(createUser(a.userName)).id
          val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, userId.sessionId))
          val id = await(invitationsRepository.create(sessionId.userId, channelId, userId.sessionId))
          (id, a.copy(id = userId))
        }).reverse

        val result1 = await(invitationsRepository.find(None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i)._1)
          assert(r.user.id == creates(i)._2.id)
        }

        val result2 = await(invitationsRepository.find(result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i + result1.size)._1)
          assert(r.user.id == creates(i + result1.size)._2.id)
        }

        val result3 = await(invitationsRepository.find(result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }
  }

  feature("accept") {

    scenario("should accept a invitation") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId, channelId, sessionId))
        await(invitationsRepository.accept(invitationId, userId.sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId), true)
        assertFutureValue(invitationsDAO.exists(userId, channelId), false)
        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if an invitation not exist") {
      forOne(userGen) { (a) =>
        val userId = await(createUser(a.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.accept(InvitationId(0), userId.sessionId))
        }.error == InvitationNotFound)

      }
    }
  }

  feature("reject") {

    scenario("should delete a friend request") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(userId, channelId, sessionId))
        await(invitationsRepository.reject(invitationId, userId.sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId), false)
        assertFutureValue(invitationsDAO.exists(userId, channelId), false)
      }
    }

    scenario("should return exception if friendRequest not exist") {
      forOne(userGen) { (a) =>
        val userId = await(createUser(a.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.reject(InvitationId(0), userId.sessionId))
        }.error == InvitationNotFound)

      }
    }
  }

}

