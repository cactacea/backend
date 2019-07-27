package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType, MessageType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class GroupAccountsRepositorySpec extends RepositorySpec {


  feature("create - join to a group") {

    scenario("should add an account to everyone group") {
      forOne(accountGen, accountGen, accountGen, everyoneGroupGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId2.toSessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId2), true)

        val messages = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))

      }
    }

    scenario("should add an account to follower group") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId3.toSessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to follow group") {
      forOne(accountGen, accountGen, accountGen, accountGen, followGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId3.toSessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to friend group") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId3.toSessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId2.toSessionId))
        }.error == AuthorityNotFound)

        assertFutureValue(accountGroupsDAO.exists(groupId, accountId3), true)
      }
    }

    scenario("should create a join message") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId.toSessionId))

        // result
        val messages = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if account joined") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a1, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        }.error == AccountAlreadyJoined)

      }
    }

    scenario("should return exception if invite only group") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, true, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        }.error == InvitationGroupFound)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId2.toSessionId))
        }.error == InvitationGroupFound)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(groupId, accountId3.toSessionId))
        }.error == InvitationGroupFound)
      }
    }

  }





  feature("create - add an account to a group") {

    scenario("should add an account to everyone group") {
      forOne(accountGen, accountGen, accountGen, everyoneGroupGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId1, groupId, sessionId))
        await(groupAccountsRepository.create(accountId2, groupId, sessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId2), true)

        val messages = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))

      }
    }

    scenario("should add an account to follower group") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId1, groupId, sessionId))
        await(groupAccountsRepository.create(accountId3, groupId, sessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(accountId2, groupId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to follow group") {
      forOne(accountGen, accountGen, accountGen, accountGen, followGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId1, groupId, sessionId))
        await(groupAccountsRepository.create(accountId3, groupId, sessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId1), true)
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId3), true)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(accountId2, groupId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to friend group") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId3, groupId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(accountId1, groupId, sessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(accountId2, groupId, sessionId))
        }.error == AuthorityNotFound)

        assertFutureValue(accountGroupsDAO.exists(groupId, accountId3), true)
      }
    }

    scenario("should create a join message") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId, groupId, sessionId))

        // result
        val messages = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen, everyoneGroupGen) { (s, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(AccountId(0), groupId, sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account joined") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a1, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(accountId1, groupId, sessionId))
        }.error == AccountAlreadyJoined)

      }
    }

    scenario("should return exception if has not authority") {
      forOne(accountGen, accountGen, accountGen, organizerGroupGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, true, GroupPrivacyType.everyone, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId1, groupId, sessionId))

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.create(accountId2, groupId, accountId1.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

  }





  feature("delete - leave from a group") {

    scenario("should delete an account group") {
      forOne(accountGen, accountGen, accountGen, everyoneGroupGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId2.toSessionId))
        await(groupAccountsRepository.delete(groupId, accountId2.toSessionId))

        // result
        val result1 = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(result1.headOption.exists(_.messageType == MessageType.left))

        val result2 = await(accountGroupsRepository.find(None, 0, 10, false, accountId2.toSessionId))
        assert(result2.size == 0)
        val result3 = await(accountGroupsRepository.find(None, 0, 10, true, accountId2.toSessionId))
        assert(result3.size == 0)
      }
    }

    scenario("should delete a group if account less count equal 0") {
      forOne(accountGen, accountGen, accountGen, everyoneGroupGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(invitationsRepository.create(accountId1, groupId, sessionId))
        await(invitationsRepository.create(accountId2, groupId, sessionId))
        await(groupAccountsRepository.delete(groupId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupsRepository.find(groupId, sessionId))
        }.error == GroupNotFound)

        val result1 = await(invitationsRepository.find(None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)
        val result2 = await(invitationsRepository.find(None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 0)
      }
    }


    scenario("should exception if session account not joined") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.delete(groupId, accountId1.toSessionId))
        }.error == AccountNotJoined)
      }
    }


    scenario("should exception if organizer and member account not only one") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId1, groupId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.delete(groupId, sessionId))
        }.error == OrganizerCanNotLeave)
      }
    }

  }



  feature("delete - leave an account from a group") {

    scenario("should delete an account group") {
      forOne(accountGen, accountGen, accountGen, everyoneGroupGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId2.toSessionId))
        await(groupAccountsRepository.delete(accountId2, groupId, sessionId))

        // result
        val result1 = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(result1.headOption.exists(_.messageType == MessageType.left))

        val result2 = await(accountGroupsRepository.find(None, 0, 10, false, accountId2.toSessionId))
        assert(result2.size == 0)
        val result3 = await(accountGroupsRepository.find(None, 0, 10, true, accountId2.toSessionId))
        assert(result3.size == 0)
      }
    }

    scenario("should delete a group if account less count equal 0") {
      forOne(accountGen, accountGen, accountGen, everyoneGroupGen) { (s, a1, a2, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, GroupAuthorityType.member, sessionId))
        await(invitationsRepository.create(accountId1, groupId, sessionId))
        await(invitationsRepository.create(accountId2, groupId, sessionId))
        await(groupAccountsRepository.delete(sessionId.toAccountId, groupId, accountId1.toSessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupsRepository.find(groupId, sessionId))
        }.error == GroupNotFound)

        val result1 = await(invitationsRepository.find(None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)
        val result2 = await(invitationsRepository.find(None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 0)
      }
    }


    scenario("should exception if session account not joined") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.delete(accountId1, groupId, sessionId))
        }.error == AccountNotJoined)
      }
    }


    scenario("should exception if organizer and member account not only one") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a1, g) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a1.accountName)).id
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, GroupAuthorityType.member, sessionId))
        await(groupAccountsRepository.create(accountId, groupId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.delete(sessionId.toAccountId, groupId, accountId.toSessionId))
        }.error == OrganizerCanNotLeave)
      }
    }


    scenario("should return exception if has not authority") {
      forOne(accountGen, accountGen, accountGen, organizerGroupGen) { (s, a1, a2, g) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, true, GroupPrivacyType.everyone, g.authorityType, sessionId))
        await(groupAccountsRepository.create(accountId1, groupId, sessionId))
        await(groupAccountsRepository.create(accountId2, groupId, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.delete(accountId2, groupId, accountId1.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

  }

  feature("find") {
    
    scenario("should return account list") {
      forOne(accountGen, accounts20ListGen, everyoneGroupGen) { (s, l, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val accountIds = l.map({ a =>
          val accountId = await(accountsRepository.create(a.accountName)).id
          await(groupAccountsRepository.create(accountId, groupId, sessionId))
          accountId
        }).reverse

        // page1 found
        val result1 = await(groupAccountsRepository.find(groupId, None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (a, i) =>
          assert(a.id == accountIds(i))
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(groupAccountsRepository.find(groupId, result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (a, i) =>
          assert(a.id == accountIds(i + size1))
        }

        // page3 found
        val result3 = await(groupAccountsRepository.find(groupId, result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 1)

      }
    }





    scenario("should add an account to follower group") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId3.toSessionId))

        // result
        await(groupAccountsRepository.find(groupId, None, 0, 10, accountId1.toSessionId))
        await(groupAccountsRepository.find(groupId, None, 0, 10, accountId3.toSessionId))

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.find(groupId, None, 0, 10, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to follow group") {
      forOne(accountGen, accountGen, accountGen, accountGen, followGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId1.toSessionId))
        await(groupAccountsRepository.create(groupId, accountId3.toSessionId))

        // result
        await(groupAccountsRepository.find(groupId, None, 0, 10, accountId1.toSessionId))
        await(groupAccountsRepository.find(groupId, None, 0, 10, accountId3.toSessionId))

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.find(groupId, None, 0, 10, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should add an account to friend group") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendGroupGen) { (s, a1, a2, a3, g) =>

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
        val groupId = await(groupsRepository.create(g.name, false, g.privacyType, g.authorityType, sessionId))
        await(groupAccountsRepository.create(groupId, accountId3.toSessionId))

        // result
        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.find(groupId, None, 0, 10, accountId1.toSessionId))
        }.error == AuthorityNotFound)

        assert(intercept[CactaceaException] {
          await(groupAccountsRepository.find(groupId, None, 0, 10, accountId2.toSessionId))
        }.error == AuthorityNotFound)

        await(groupAccountsRepository.find(groupId, None, 0, 10, accountId3.toSessionId))
      }
    }



  }

}
