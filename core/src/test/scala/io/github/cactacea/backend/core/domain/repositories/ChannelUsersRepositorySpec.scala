package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class ChannelUsersRepositorySpec extends RepositorySpec {


  feature("create - join to a channel") {

    scenario("should add an user to everyone channel") {
      forOne(userGen, userGen, userGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId2.sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId2), true)

        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))

      }
    }

    scenario("should add an user to follower channel") {
      forOne(userGen, userGen, userGen, userGen, followerChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId3.sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId3), true)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId2.sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an user to follow channel") {
      forOne(userGen, userGen, userGen, userGen, followChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId3.sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId3), true)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId2.sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an user to friend channel") {
      forOne(userGen, userGen, userGen, userGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId3.sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId1.sessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId2.sessionId))
        }.error == AuthorityNotFound)

        assertFutureValue(userChannelsDAO.exists(channelId, userId3), true)
      }
    }

    scenario("should create a join message") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId.sessionId))

        // result
        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if user joined") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a1, g) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId1.sessionId))
        }.error == UserAlreadyJoined)

      }
    }

    scenario("should return exception if invite only channel") {
      forOne(userGen, userGen, userGen, userGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, true, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId1.sessionId))
        }.error == InvitationChannelFound)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId2.sessionId))
        }.error == InvitationChannelFound)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(channelId, userId3.sessionId))
        }.error == InvitationChannelFound)
      }
    }

  }





  feature("create - add an user to a channel") {

    scenario("should add an user to everyone channel") {
      forOne(userGen, userGen, userGen, everyoneChannelGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId1, channelId, sessionId))
        await(channelUsersRepository.create(userId2, channelId, sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId2), true)

        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))

      }
    }

    scenario("should add an user to follower channel") {
      forOne(userGen, userGen, userGen, userGen, followerChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId1, channelId, sessionId))
        await(channelUsersRepository.create(userId3, channelId, sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId3), true)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(userId2, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an user to follow channel") {
      forOne(userGen, userGen, userGen, userGen, followChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId1, channelId, sessionId))
        await(channelUsersRepository.create(userId3, channelId, sessionId))

        // result
        assertFutureValue(userChannelsDAO.exists(channelId, userId1), true)
        assertFutureValue(userChannelsDAO.exists(channelId, userId3), true)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(userId2, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an user to friend channel") {
      forOne(userGen, userGen, userGen, userGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId3, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(userId1, channelId, sessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(userId2, channelId, sessionId))
        }.error == AuthorityNotFound)

        assertFutureValue(userChannelsDAO.exists(channelId, userId3), true)
      }
    }

    scenario("should create a join message") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId, channelId, sessionId))

        // result
        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen, everyoneChannelGen) { (s, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(UserId(0), channelId, sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if user joined") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a1, g) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(userId1, channelId, sessionId))
        }.error == UserAlreadyJoined)

      }
    }

    scenario("should return exception if has not authority") {
      forOne(userGen, userGen, userGen, organizerChannelGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, true, ChannelPrivacyType.everyone, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId1, channelId, sessionId))

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.create(userId2, channelId, userId1.sessionId))
        }.error == AuthorityNotFound)
      }
    }

  }





  feature("delete - leave from a channel") {

    scenario("should delete an user channel") {
      forOne(userGen, userGen, userGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId2.sessionId))
        await(channelUsersRepository.delete(channelId, userId2.sessionId))

        // result
        val result1 = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(result1.headOption.exists(_.messageType == MessageType.left))

        val result2 = await(userChannelsRepository.find(None, 0, 10, false, userId2.sessionId))
        assert(result2.size == 0)
        val result3 = await(userChannelsRepository.find(None, 0, 10, true, userId2.sessionId))
        assert(result3.size == 0)
      }
    }

    scenario("should delete a channel if user less count equal 0") {
      forOne(userGen, userGen, userGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(invitationsRepository.create(userId1, channelId, sessionId))
        await(invitationsRepository.create(userId2, channelId, sessionId))
        await(channelUsersRepository.delete(channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelsRepository.find(channelId, sessionId))
        }.error == ChannelNotFound)

        val result1 = await(invitationsRepository.find(None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)
        val result2 = await(invitationsRepository.find(None, 0, 10, userId2.sessionId))
        assert(result2.size == 0)
      }
    }


    scenario("should exception if session user not joined") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.delete(channelId, userId1.sessionId))
        }.error == UserNotJoined)
      }
    }


    scenario("should exception if organizer and member user not only one") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, ChannelAuthorityType.organizer, sessionId))
        await(channelUsersRepository.create(userId1, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.delete(channelId, sessionId))
        }.error == OrganizerCanNotLeave)
      }
    }

  }



  feature("delete - leave an user from a channel") {

    scenario("should delete an user channel") {
      forOne(userGen, userGen, userGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId2.sessionId))
        await(channelUsersRepository.delete(userId2, channelId, sessionId))

        // result
        val result1 = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(result1.headOption.exists(_.messageType == MessageType.left))

        val result2 = await(userChannelsRepository.find(None, 0, 10, false, userId2.sessionId))
        assert(result2.size == 0)
        val result3 = await(userChannelsRepository.find(None, 0, 10, true, userId2.sessionId))
        assert(result3.size == 0)
      }
    }

    scenario("should delete a channel if user less count equal 0") {
      forOne(userGen, userGen, userGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, ChannelAuthorityType.member, sessionId))
        await(invitationsRepository.create(userId1, channelId, sessionId))
        await(invitationsRepository.create(userId2, channelId, sessionId))
        await(channelUsersRepository.delete(channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelsRepository.find(channelId, sessionId))
        }.error == ChannelNotFound)

        val result1 = await(invitationsRepository.find(None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)
        val result2 = await(invitationsRepository.find(None, 0, 10, userId2.sessionId))
        assert(result2.size == 0)
      }
    }


    scenario("should exception if session user not joined") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.delete(userId1, channelId, sessionId))
        }.error == UserNotJoined)
      }
    }


    scenario("should not exception if organizer delete") {
      forOne(userGen, userGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a1.userName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, ChannelAuthorityType.organizer, sessionId))
        await(channelUsersRepository.create(userId, channelId, sessionId))
        await(userChannelsDAO.updateAuthorityType(channelId, ChannelAuthorityType.organizer, userId.sessionId))
        await(channelUsersRepository.delete(sessionId.userId, channelId, userId.sessionId))
      }
    }


    scenario("should return exception if has not authority") {
      forOne(userGen, userGen, userGen, organizerChannelGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val channelId = await(channelsRepository.create(g.name, true, ChannelPrivacyType.everyone, g.authorityType, sessionId))
        await(channelUsersRepository.create(userId1, channelId, sessionId))
        await(channelUsersRepository.create(userId2, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.delete(userId2, channelId, userId1.sessionId))
        }.error == AuthorityNotFound)
      }
    }

  }

  feature("find") {
    
    scenario("should return user list") {
      forOne(userGen, user20ListGen, everyoneChannelGen) { (s, l, g) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val userIds = l.map({ a =>
          val userId = await(createUser(a.userName)).id
          await(channelUsersRepository.create(userId, channelId, sessionId))
          userId
        }).reverse

        // page1 found
        val result1 = await(channelUsersRepository.find(channelId, None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (a, i) =>
          assert(a.id == userIds(i))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(channelUsersRepository.find(channelId, result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (a, i) =>
          assert(a.id == userIds(i + size1))
        }

        // page3 found
        val result3 = await(channelUsersRepository.find(channelId, result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 1)

      }
    }





    scenario("should add an user to follower channel") {
      forOne(userGen, userGen, userGen, userGen, followerChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId3.sessionId))

        // result
        await(channelUsersRepository.find(channelId, None, 0, 10, userId1.sessionId))
        await(channelUsersRepository.find(channelId, None, 0, 10, userId3.sessionId))

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.find(channelId, None, 0, 10, userId2.sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an user to follow channel") {
      forOne(userGen, userGen, userGen, userGen, followChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId1.sessionId))
        await(channelUsersRepository.create(channelId, userId3.sessionId))

        // result
        await(channelUsersRepository.find(channelId, None, 0, 10, userId1.sessionId))
        await(channelUsersRepository.find(channelId, None, 0, 10, userId3.sessionId))

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.find(channelId, None, 0, 10, userId2.sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an user to friend channel") {
      forOne(userGen, userGen, userGen, userGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   user1 is follow user
        //   user3 is friend user
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, sessionId))
        val requestId = await(friendRequestsRepository.create(userId3, sessionId))
        await(friendRequestsRepository.accept(requestId, userId3.sessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelUsersRepository.create(channelId, userId3.sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelUsersRepository.find(channelId, None, 0, 10, userId1.sessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(channelUsersRepository.find(channelId, None, 0, 10, userId2.sessionId))
        }.error == AuthorityNotFound)

        await(channelUsersRepository.find(channelId, None, 0, 10, userId3.sessionId))
      }
    }



  }

}
