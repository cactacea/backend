package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class FollowsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should follow an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, accountId2.toSessionId))
        await(followsRepository.create(accountId2, accountId3.toSessionId))
        await(followsRepository.create(accountId3, accountId1.toSessionId))
        assertFutureValue(followsDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(followsDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(followsDAO.own(accountId2, accountId3.toSessionId), true)
        val result1 = await(accountsRepository.find(accountId1.toSessionId))
        val result2 = await(accountsRepository.find(accountId2.toSessionId))
        val result3 = await(accountsRepository.find(accountId3.toSessionId))
        assert(result1.followCount == 1L)
        assert(result2.followCount == 1L)
        assert(result3.followCount == 1L)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (a1) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.create(accountId1, accountId1.toSessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen) { (a1) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.create(AccountId(0), accountId1.toSessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account already followed") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        await(followsRepository.create(accountId1, accountId2.toSessionId))
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.create(accountId1, accountId2.toSessionId))
        }.error == AccountAlreadyFollowed)
      }
    }


  }

  feature("delete") {
    scenario("should unfollow an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(followsRepository.create(accountId1, accountId2.toSessionId))
        await(followsRepository.create(accountId2, accountId3.toSessionId))
        await(followsRepository.create(accountId3, accountId1.toSessionId))
        await(followsRepository.delete(accountId1, accountId2.toSessionId))
        await(followsRepository.delete(accountId2, accountId3.toSessionId))
        await(followsRepository.delete(accountId3, accountId1.toSessionId))
        assertFutureValue(followsDAO.own(accountId1, accountId2.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId2, accountId3.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId3, accountId1.toSessionId), false)
        val result1 = await(accountsRepository.find(accountId1.toSessionId))
        val result2 = await(accountsRepository.find(accountId2.toSessionId))
        val result3 = await(accountsRepository.find(accountId3.toSessionId))
        assert(result1.followCount == 0L)
        assert(result2.followCount == 0L)
        assert(result3.followCount == 0L)
      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (a1) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.delete(accountId1, accountId1.toSessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen) { (a1) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.delete(AccountId(0), accountId1.toSessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account not followed") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.delete(accountId1, accountId2.toSessionId))
        }.error == AccountNotFollowed)
      }
    }

  }

  feature("find") {

    scenario("should return session follow list") {
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
        await(followsRepository.create(accountId1, sessionId))
        await(followsRepository.create(accountId2, sessionId))
        await(followsRepository.create(accountId3, sessionId))
        await(followsRepository.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followsRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(followsRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }

    scenario("should return an account's follow list") {
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
        await(followsRepository.create(accountId1, sessionId))
        await(followsRepository.create(accountId2, sessionId))
        await(followsRepository.create(accountId3, sessionId))
        await(followsRepository.create(accountId4, sessionId))

        // account2 block account1
        await(blocksRepository.create(accountId1, accountId2.toSessionId))

        // return account1 found
        // return account2 not found because of account2 be blocked by account1
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followsRepository.find(sessionId.toAccountId, Option(h), None, 0, 2, accountId1.toSessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId1)

        val result2 = await(followsRepository.find(sessionId.toAccountId, Option(h), result1.lastOption.map(_.next), 0, 2, accountId1.toSessionId))
        assert(result2.size == 0)
      }
    }

  scenario("should return exception if an account not exist") {
      forOne(accountGen) { (a1) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.find(AccountId(0), None, None, 0, 2, accountId1.toSessionId))
        }.error == AccountNotFound)
      }
    }
  }

}
