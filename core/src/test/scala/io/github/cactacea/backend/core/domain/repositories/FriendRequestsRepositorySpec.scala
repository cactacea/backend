package io.github.cactacea.backend.core.domain.repositories

import java.util.Locale
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FriendRequestId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class FriendRequestsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a friend friendRequest") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val friendRequestId = await(friendRequestsRepository.create(accountId1, sessionId))

        // result
        val result1 = await(friendRequestsRepository.find(None, 0, 10, true, accountId1.toSessionId))
        assert(result1.headOption.exists(_.id == friendRequestId))
        assert(result1.headOption.exists(_.account.id == sessionId.toAccountId))

        val result2 = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), accountId1.toSessionId))
        assert(result2.headOption.exists(_.contentId.exists(_ == friendRequestId.value)))
        assert(result2.headOption.exists(_.notificationType == NotificationType.friendRequest))
      }
    }


    scenario("should return exception if id is same") {
      forOne(accountGen) { (s) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.create(sessionId.toAccountId, sessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen) { (s) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.create(AccountId(0), sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if already requested") {
      forOne(accountGen, accountGen) { (s, a1) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        await(friendRequestsRepository.create(accountId1, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.create(accountId1, sessionId))
        }.error == AccountAlreadyRequested)
      }
    }

  }


  feature("delete") {
    
    scenario("should delete a friend friendRequest") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val friendRequestId = await(friendRequestsRepository.create(accountId1, sessionId))

        // result
        val result1 = await(findFriendRequest(friendRequestId))
        assert(result1.isDefined)
        await(friendRequestsRepository.delete(accountId1, sessionId))
        val result2 = await(findFriendRequest(friendRequestId))
        assert(result2.isEmpty)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (s) =>
        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.delete(sessionId.toAccountId, sessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen) { (s) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.delete(AccountId(0), sessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if not requested") {
      forOne(accountGen, accountGen) { (s, a1) =>
        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.delete(accountId1, sessionId))
        }.error == FriendRequestNotFound)
      }
    }

  }


  feature("find requests") {

    scenario("should return received friendRequest") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        val accountId5 = await(accountsRepository.create(a5.accountName)).id
        val id1 = await(friendRequestsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        val id2 = await(friendRequestsRepository.create(sessionId.toAccountId, accountId2.toSessionId))
        val id3 = await(friendRequestsRepository.create(sessionId.toAccountId, accountId3.toSessionId))
        val id4 = await(friendRequestsRepository.create(sessionId.toAccountId, accountId4.toSessionId))
        await(friendRequestsRepository.create(accountId5, sessionId))

        val result1 = await(friendRequestsRepository.find(None, 0, 3, true, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).account.id == accountId4)
        assert(result1(1).id == id3)
        assert(result1(1).account.id == accountId3)
        assert(result1(2).id == id2)
        assert(result1(2).account.id == accountId2)

        val result2 = await(friendRequestsRepository.find(result1.lastOption.map(_.next), 0, 3, true, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).account.id == accountId1)
      }
    }

    scenario("should return friendRequest account send") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        val accountId5 = await(accountsRepository.create(a5.accountName)).id
        val id1 = await(friendRequestsRepository.create(accountId1, sessionId))
        val id2 = await(friendRequestsRepository.create(accountId2, sessionId))
        val id3 = await(friendRequestsRepository.create(accountId3, sessionId))
        val id4 = await(friendRequestsRepository.create(accountId4, sessionId))
        await(friendRequestsRepository.create(sessionId.toAccountId, accountId5.toSessionId))

        val result1 = await(friendRequestsRepository.find(None, 0, 3, false, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).account.id == accountId4)
        assert(result1(1).id == id3)
        assert(result1(1).account.id == accountId3)
        assert(result1(2).id == id2)
        assert(result1(2).account.id == accountId2)

        val result2 = await(friendRequestsRepository.find(result1.lastOption.map(_.next), 0, 3, false, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).account.id == accountId1)
      }
    }

  }

  feature("accept") {
    scenario("should delete a friendRequest") {
      forOne(accountGen, accountGen) { (s, a1) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val friendRequestId = await(friendRequestsRepository.create(accountId1, sessionId))
        await(friendRequestsRepository.accept(friendRequestId, accountId1.toSessionId))

        // result
        val result = await(friendsRepository.find(None, None, 0, 10, accountId1.toSessionId))
        assert(result.headOption.exists(_.id == sessionId.toAccountId))
        assertFutureValue(friendRequestsDAO.own(accountId1, sessionId), false)
      }
    }

    scenario("should return exception if friendRequest not exist") {
      forOne(accountGen) { (s) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.accept(FriendRequestId(0), sessionId))
        }.error == FriendRequestNotFound)
      }
    }

  }

  feature("reject") {
    scenario("should delete a friend friendRequest") {
      forOne(accountGen, accountGen) { (s, a1) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val friendRequestId = await(friendRequestsRepository.create(accountId1, sessionId))
        await(friendRequestsRepository.reject(friendRequestId, accountId1.toSessionId))

        // result
        assertFutureValue(friendRequestsDAO.own(accountId1, sessionId), false)
      }
    }
    scenario("should return exception if friendRequest not exist") {
      forOne(accountGen) { (s) =>
        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.reject(FriendRequestId(0), sessionId))
        }.error == FriendRequestNotFound)
      }
    }
  }


}

