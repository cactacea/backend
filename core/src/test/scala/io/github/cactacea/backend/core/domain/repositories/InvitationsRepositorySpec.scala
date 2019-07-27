package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, InvitationId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class InvitationsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a invitation") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val id = await(invitationsRepository.create(accountId, groupId, sessionId))
        val result = await(invitationsDAO.find(accountId, id))
        assert(result.isDefined)
      }
    }

    scenario("should return exception if already requested") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        await(invitationsRepository.create(accountId, groupId, sessionId))
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId, groupId, sessionId))
        }.error == AccountAlreadyInvited)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen, everyoneGroupGen) { (s, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(sessionId.toAccountId, groupId, sessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen, everyoneGroupGen) { (s, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(AccountId(0), groupId, sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if group not exist") {
      forOne(accountGen, accountGen) { (s, a) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId, GroupId(0), sessionId))
        }.error == GroupNotFound)

      }
    }

    scenario("should create a invitation if friend") {
      forOne(accountGen, accountGen, friendGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val requestId = await(friendRequestsRepository.create(accountId, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId.toSessionId))
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId, groupId, sessionId))
        assert(await(invitationsDAO.find(accountId, invitationId)).isDefined)
        await(invitationsRepository.accept(invitationId, accountId.toSessionId))
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId), true)
      }
    }

    scenario("should create a invitation if follower") {
      forOne(accountGen, accountGen, followerGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId.toSessionId))
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId, groupId, sessionId))
        assert(await(invitationsDAO.find(accountId, invitationId)).isDefined)
        await(invitationsRepository.accept(invitationId, accountId.toSessionId))
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId), true)
      }
    }

    scenario("should create a invitation if follow") {
      forOne(accountGen, accountGen, followGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        await(followsRepository.create(accountId, sessionId))
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId, groupId, sessionId))
        assert(await(invitationsDAO.find(accountId, invitationId)).isDefined)
        await(invitationsRepository.accept(invitationId, accountId.toSessionId))
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId), true)
      }
    }



    scenario("should return exception if invite not friend account") {
      forOne(accountGen, accountGen, friendGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId, groupId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should return exception if invite not follower account") {
      forOne(accountGen, accountGen, followerGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId, groupId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should return exception if invite not follow account") {
      forOne(accountGen, accountGen, followGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId, groupId, sessionId))
        }.error == AuthorityNotFound)
      }
    }

    scenario("should return exception if member authority not exist") {
      forOne(accountGen, accountGen, accountGen, memberGroupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId1, groupId, accountId2.toSessionId))
        }.error == AccountNotJoined)
      }
    }

    scenario("should return exception if organizer authority not exist") {
      forOne(accountGen, accountGen, accountGen, organizerGroupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId2, groupId, sessionId))
        await(invitationsRepository.accept(invitationId, accountId2.toSessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId1, groupId, accountId2.toSessionId))
        }.error == AuthorityNotFound)
      }
    }


    scenario("should return exception if group is direct message") {
      forOne(accountGen, accountGen, accountGen, followerGroupGen) { (s, a1, a2, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val groupId = await(accountGroupsRepository.findOrCreate(accountId1, sessionId)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.create(accountId2, groupId, sessionId))
        }.error == AuthorityNotFound)
      }
    }


  }


  feature("delete") {

    scenario("authority type is member") (pending)
    scenario("authority type is organizer") (pending)

    scenario("should delete a invitation") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId, groupId, sessionId))
        await(invitationsRepository.delete(accountId, groupId, sessionId))
        val result = await(invitationsDAO.find(accountId, invitationId))
        assert(result.isEmpty)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen, everyoneGroupGen) { (s, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.delete(sessionId.toAccountId, groupId, sessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen, everyoneGroupGen) { (s, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.delete(AccountId(0), groupId, sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if group not exist") {
      forOne(accountGen, accountGen) { (s, a) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.delete(accountId, GroupId(0), sessionId))
        }.error == GroupNotFound)

      }
    }
  }

  feature("find") {
    scenario("should return received invitations") {
      forOne(accountGen, accounts20ListGen, everyoneGroupGen) { (s, l, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val creates = l.map({a =>
          val accountId = await(accountsRepository.create(a.accountName)).id
          val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, accountId.toSessionId))
          val id = await(invitationsRepository.create(sessionId.toAccountId, groupId, accountId.toSessionId))
          (id, a.copy(id = accountId))
        }).reverse

        val result1 = await(invitationsRepository.find(None, 0, 10, sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i)._1)
          assert(r.account.id == creates(i)._2.id)
        }

        val result2 = await(invitationsRepository.find(result1.lastOption.map(_.next), 0, 10, sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == creates(i + result1.size)._1)
          assert(r.account.id == creates(i + result1.size)._2.id)
        }

        val result3 = await(invitationsRepository.find(result2.lastOption.map(_.next), 0, 10, sessionId))
        assert(result3.size == 0)

      }
    }
  }

  feature("accept") {

    scenario("should accept a invitation") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId, groupId, sessionId))
        await(invitationsRepository.accept(invitationId, accountId.toSessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId), true)
        assertFutureValue(invitationsDAO.exists(accountId, groupId), false)
        val messages = await(messagesRepository.find(groupId, None, 0, 10, false, sessionId))
        assert(messages.headOption.exists(_.messageType == MessageType.joined))
      }
    }

    scenario("should return exception if an invitation not exist") {
      forOne(accountGen) { (a) =>
        val accountId = await(accountsRepository.create(a.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.accept(InvitationId(0), accountId.toSessionId))
        }.error == InvitationNotFound)

      }
    }
  }

  feature("reject") {

    scenario("should delete a friend friendRequest") {
      forOne(accountGen, accountGen, everyoneGroupGen) { (s, a, g) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val groupId = await(groupsRepository.create(g.name, g.invitationOnly, g.privacyType, g.authorityType, sessionId))
        val invitationId = await(invitationsRepository.create(accountId, groupId, sessionId))
        await(invitationsRepository.reject(invitationId, accountId.toSessionId))

        // result
        assertFutureValue(accountGroupsDAO.exists(groupId, accountId), false)
        assertFutureValue(invitationsDAO.exists(accountId, groupId), false)
      }
    }

    scenario("should return exception if friendRequest not exist") {
      forOne(accountGen) { (a) =>
        val accountId = await(accountsRepository.create(a.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(invitationsRepository.reject(InvitationId(0), accountId.toSessionId))
        }.error == InvitationNotFound)

      }
    }
  }

}

