package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, InvalidAccountIdError}

class FriendsRepositorySpec extends RepositorySpec {

  feature("delete") {

    scenario("should delete a friend") {
      forAll(accountGen, accountGen) { (a1, a2) =>
        val sessionId = await(accountsRepository.create(a1.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a2.accountName)).id
        val requestId = await(friendRequestsRepository.create(accountId, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId.toSessionId))

        assert(await(friendsDAO.own(accountId, sessionId)))
        assert(await(friendsDAO.own(sessionId.toAccountId, accountId.toSessionId)))
        assert(await(accountsRepository.find(sessionId)).friendCount == 1L)
        assert(await(accountsRepository.find(accountId.toSessionId)).friendCount == 1L)
        await(friendsRepository.delete(accountId, sessionId))

        assert(!await(friendsDAO.own(accountId, sessionId)))
        assert(!await(friendsDAO.own(sessionId.toAccountId, accountId.toSessionId)))
        assert(await(accountsRepository.find(sessionId)).friendCount == 0L)
        assert(await(accountsRepository.find(accountId.toSessionId)).friendCount == 0L)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.delete(sessionId.toAccountId, sessionId))
        }.error == InvalidAccountIdError)

      }
    }

    scenario("should return exception if account is not exist") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.delete(AccountId(0), sessionId))
        }.error == AccountNotFound)

      }
    }

  }

  feature("find") {

    scenario("should return session friend list") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account follow account1
        //   session account follow account2
        //   session account follow account3
        //   session account follow account4
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(h + a1.accountName)).id
        val accountId2 = await(accountsRepository.create(h + a2.accountName)).id
        val accountId3 = await(accountsRepository.create(h + a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        val requestId1 = await(friendRequestsRepository.create(accountId1, sessionId))
        val requestId2 = await(friendRequestsRepository.create(accountId2, sessionId))
        val requestId3 = await(friendRequestsRepository.create(accountId3, sessionId))
        val requestId4 = await(friendRequestsRepository.create(accountId4, sessionId))
        await(friendRequestsRepository.accept(requestId1, accountId1.toSessionId))
        await(friendRequestsRepository.accept(requestId2, accountId2.toSessionId))
        await(friendRequestsRepository.accept(requestId3, accountId3.toSessionId))
        await(friendRequestsRepository.accept(requestId4, accountId4.toSessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(friendsRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(friendsRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }

    scenario("should return an account's friend list") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account follow account1
        //   session account follow account2
        //   session account follow account3
        //   session account follow account4
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(h + a1.accountName)).id
        val accountId2 = await(accountsRepository.create(h + a2.accountName)).id
        val accountId3 = await(accountsRepository.create(h + a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        val requestId1 = await(friendRequestsRepository.create(accountId1, sessionId))
        val requestId2 = await(friendRequestsRepository.create(accountId2, sessionId))
        val requestId3 = await(friendRequestsRepository.create(accountId3, sessionId))
        val requestId4 = await(friendRequestsRepository.create(accountId4, sessionId))
        await(friendRequestsRepository.accept(requestId1, accountId1.toSessionId))
        await(friendRequestsRepository.accept(requestId2, accountId2.toSessionId))
        await(friendRequestsRepository.accept(requestId3, accountId3.toSessionId))
        await(friendRequestsRepository.accept(requestId4, accountId4.toSessionId))

        // account2 block account1
        await(blocksRepository.create(accountId1, accountId2.toSessionId))

        // return account1 found
        // return account2 not found because of account2 be blocked by account1
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(friendsRepository.find(sessionId.toAccountId, Option(h), None, 0, 2, accountId1.toSessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId1)

        val result2 = await(friendsRepository.find(sessionId.toAccountId, Option(h), result1.lastOption.map(_.next), 0, 2, accountId1.toSessionId))
        assert(result2.size == 0)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.find(sessionId.toAccountId, None, None, 0, 2, sessionId))
        }.error == InvalidAccountIdError)

      }
    }

    scenario("should return exception if account is not exist") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.find(AccountId(0), None, None, 0, 2, sessionId))
        }.error == AccountNotFound)

      }
    }

  }


}
