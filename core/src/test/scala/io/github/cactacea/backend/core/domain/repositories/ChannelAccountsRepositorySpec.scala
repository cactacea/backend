package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class ChannelAccountsRepositorySpec extends RepositorySpec {


  feature("create - join to a channel") {

    scenario("should add an account to everyone channel") {
      forOne(accountGen, accountGen, accountGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId2.toSessionId))

        // result
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId2), true)

        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))

      }
    }

    scenario("should add an account to follower channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId3.toSessionId))

        // result
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to follow channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, followChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId3.toSessionId))

        // result
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to friend channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId3.toSessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId2.toSessionId))
        }.error == AuthorityNotFound)

        assertFutureValue(accountChannelsDAO.exists(channelId, accountId3), true)
      }
    }

    scenario("should create a join message") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId.toSessionId))

        // result
        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if account joined") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a1, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        }.error == AccountAlreadyJoined)

      }
    }

    scenario("should return exception if invite only channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, true, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        }.error == InvitationChannelFound)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId2.toSessionId))
        }.error == InvitationChannelFound)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(channelId, accountId3.toSessionId))
        }.error == InvitationChannelFound)
      }
    }

  }





  feature("create - add an account to a channel") {

    scenario("should add an account to everyone channel") {
      forOne(accountGen, accountGen, accountGen, everyoneChannelGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId1, channelId, sessionId))
        await(channelAccountsRepository.create(accountId2, channelId, sessionId))

        // result
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId2), true)

        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))

      }
    }

    scenario("should add an account to follower channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId1, channelId, sessionId))
        await(channelAccountsRepository.create(accountId3, channelId, sessionId))

        // result
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(accountId2, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to follow channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, followChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId1, channelId, sessionId))
        await(channelAccountsRepository.create(accountId3, channelId, sessionId))

        // result
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId1), true)
        assertFutureValue(accountChannelsDAO.exists(channelId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(accountId2, channelId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to friend channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId3, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(accountId1, channelId, sessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(accountId2, channelId, sessionId))
        }.error == AuthorityNotFound)

        assertFutureValue(accountChannelsDAO.exists(channelId, accountId3), true)
      }
    }

    scenario("should create a join message") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId, channelId, sessionId))

        // result
        val messages = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen, everyoneChannelGen) { (s, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(AccountId(0), channelId, sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account joined") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a1, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(accountId1, channelId, sessionId))
        }.error == AccountAlreadyJoined)

      }
    }

    scenario("should return exception if has not authority") {
      forOne(accountGen, accountGen, accountGen, organizerChannelGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, true, ChannelPrivacyType.everyone, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId1, channelId, sessionId))

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.create(accountId2, channelId, accountId1.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

  }





  feature("delete - leave from a channel") {

    scenario("should delete an account channel") {
      forOne(accountGen, accountGen, accountGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId2.toSessionId))
        await(channelAccountsRepository.delete(channelId, accountId2.toSessionId))

        // result
        val result1 = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(result1.headOption.exists(_.messageType == MessageType.left))

        val result2 = await(accountChannelsRepository.find(None, 0, 10, false, accountId2.toSessionId))
        assert(result2.size == 0)
        val result3 = await(accountChannelsRepository.find(None, 0, 10, true, accountId2.toSessionId))
        assert(result3.size == 0)
      }
    }

    scenario("should delete a channel if account less count equal 0") {
      forOne(accountGen, accountGen, accountGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(invitationsRepository.create(accountId1, channelId, sessionId))
        await(invitationsRepository.create(accountId2, channelId, sessionId))
        await(channelAccountsRepository.delete(channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelsRepository.find(channelId, sessionId))
        }.error == ChannelNotFound)

        val result1 = await(invitationsRepository.find(None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)
        val result2 = await(invitationsRepository.find(None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 0)
      }
    }


    scenario("should exception if session account not joined") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.delete(channelId, accountId1.toSessionId))
        }.error == AccountNotJoined)
      }
    }


    scenario("should exception if organizer and member account not only one") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId1, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.delete(channelId, sessionId))
        }.error == OrganizerCanNotLeave)
      }
    }

  }



  feature("delete - leave an account from a channel") {

    scenario("should delete an account channel") {
      forOne(accountGen, accountGen, accountGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId2.toSessionId))
        await(channelAccountsRepository.delete(accountId2, channelId, sessionId))

        // result
        val result1 = await(messagesRepository.find(channelId, None, 0, 10, false, sessionId))
        assert(result1.headOption.exists(_.messageType == MessageType.left))

        val result2 = await(accountChannelsRepository.find(None, 0, 10, false, accountId2.toSessionId))
        assert(result2.size == 0)
        val result3 = await(accountChannelsRepository.find(None, 0, 10, true, accountId2.toSessionId))
        assert(result3.size == 0)
      }
    }

    scenario("should delete a channel if account less count equal 0") {
      forOne(accountGen, accountGen, accountGen, everyoneChannelGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, ChannelAuthorityType.member, sessionId))
        await(invitationsRepository.create(accountId1, channelId, sessionId))
        await(invitationsRepository.create(accountId2, channelId, sessionId))
        await(channelAccountsRepository.delete(sessionId.toAccountId, channelId, accountId1.toSessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelsRepository.find(channelId, sessionId))
        }.error == ChannelNotFound)

        val result1 = await(invitationsRepository.find(None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)
        val result2 = await(invitationsRepository.find(None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 0)
      }
    }


    scenario("should exception if session account not joined") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.delete(accountId1, channelId, sessionId))
        }.error == AccountNotJoined)
      }
    }


    scenario("should exception if organizer and member account not only one") {
      forOne(accountGen, accountGen, everyoneChannelGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a1.accountName)).id
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, ChannelAuthorityType.member, sessionId))
        await(channelAccountsRepository.create(accountId, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.delete(sessionId.toAccountId, channelId, accountId.toSessionId))
        }.error == OrganizerCanNotLeave)
      }
    }


    scenario("should return exception if has not authority") {
      forOne(accountGen, accountGen, accountGen, organizerChannelGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val channelId = await(channelsRepository.create(g.name, true, ChannelPrivacyType.everyone, g.authorityType, sessionId))
        await(channelAccountsRepository.create(accountId1, channelId, sessionId))
        await(channelAccountsRepository.create(accountId2, channelId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.delete(accountId2, channelId, accountId1.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

  }

  feature("find") {
    
    scenario("should return account list") {
      forOne(accountGen, accounts20ListGen, everyoneChannelGen) { (s, l, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val channelId = await(channelsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val accountIds = l.map({ a =>
          val accountId = await(accountsRepository.create(a.accountName)).id
          await(channelAccountsRepository.create(accountId, channelId, sessionId))
          accountId
        }).reverse

        // page1 found
        val result1 = await(channelAccountsRepository.find(channelId, None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (a, i) =>
          assert(a.id == accountIds(i))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(channelAccountsRepository.find(channelId, result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (a, i) =>
          assert(a.id == accountIds(i + size1))
        }

        // page3 found
        val result3 = await(channelAccountsRepository.find(channelId, result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 1)

      }
    }





    scenario("should add an account to follower channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId3.toSessionId))

        // result
        await(channelAccountsRepository.find(channelId, None, 0, 10, accountId1.toSessionId))
        await(channelAccountsRepository.find(channelId, None, 0, 10, accountId3.toSessionId))

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.find(channelId, None, 0, 10, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to follow channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, followChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId1.toSessionId))
        await(channelAccountsRepository.create(channelId, accountId3.toSessionId))

        // result
        await(channelAccountsRepository.find(channelId, None, 0, 10, accountId1.toSessionId))
        await(channelAccountsRepository.find(channelId, None, 0, 10, accountId3.toSessionId))

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.find(channelId, None, 0, 10, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to friend channel") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendChannelGen) { (s, a1, a2, a3, g) =>

        // preparing
        //   account1 is follow account
        //   account3 is friend account
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, sessionId))
        val requestId = await(friendRequestsRepository.create(accountId3, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId3.toSessionId))
        val channelId = await(channelsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(channelAccountsRepository.create(channelId, accountId3.toSessionId))

        // result
        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.find(channelId, None, 0, 10, accountId1.toSessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(channelAccountsRepository.find(channelId, None, 0, 10, accountId2.toSessionId))
        }.error == AuthorityNotFound)

        await(channelAccountsRepository.find(channelId, None, 0, 10, accountId3.toSessionId))
      }
    }



  }

}
