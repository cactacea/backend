package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyMuted, AccountNotFound, AccountNotMuted, InvalidAccountIdError}

class MutesRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should mute an block") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(mutesRepository.create(accountId1, accountId2.toSessionId))
        await(mutesRepository.create(accountId2, accountId3.toSessionId))
        await(mutesRepository.create(accountId3, accountId1.toSessionId))
        assertFutureValue(mutesDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(mutesDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(mutesDAO.own(accountId2, accountId3.toSessionId), true)
      }
    }

    scenario("should return exception if id is not same") {
      forOne(accountGen) { (s) =>
        val accountId2 = await(accountsRepository.create(s.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.create(accountId2, accountId2.toSessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen) { (s) =>
        val accountId2 = await(accountsRepository.create(s.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.create(AccountId(0), accountId2.toSessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account already muted") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        await(mutesRepository.create(accountId1, accountId2.toSessionId))
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.create(accountId1, accountId2.toSessionId))
        }.error == AccountAlreadyMuted)
      }
    }

  }

  feature("delete") {
    scenario("should unmute an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(mutesRepository.create(accountId2, accountId1.toSessionId))
        await(mutesRepository.create(accountId3, accountId1.toSessionId))
        await(mutesRepository.delete(accountId2, accountId1.toSessionId))
        await(mutesRepository.delete(accountId3, accountId1.toSessionId))
        assertFutureValue(mutesDAO.own(accountId2, accountId1.toSessionId), false)
        assertFutureValue(mutesDAO.own(accountId3, accountId1.toSessionId), false)
      }
    }

    scenario("should return exception if id is not same") {
      forOne(accountGen) { (s) =>
        val accountId2 = await(accountsRepository.create(s.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.delete(accountId2, accountId2.toSessionId))
        }.error == InvalidAccountIdError)
      }
    }

    scenario("should return exception if account not exist") {
      forOne(accountGen) { (s) =>
        val accountId2 = await(accountsRepository.create(s.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.delete(AccountId(0), accountId2.toSessionId))
        }.error == AccountNotFound)
      }
    }

    scenario("should return exception if account not muted") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.delete(accountId1, accountId2.toSessionId))
        }.error == AccountNotMuted)
      }
    }

  }

  feature("find") {
    scenario("should return mute list in order of creation") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account mute account1
        //   session account mute account2
        //   session account mute account3
        //   session account mute account4
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(h + a1.accountName)).id
        val accountId2 = await(accountsRepository.create(h + a2.accountName)).id
        val accountId3 = await(accountsRepository.create(h + a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        await(mutesRepository.create(accountId1, sessionId))
        await(mutesRepository.create(accountId2, sessionId))
        await(mutesRepository.create(accountId3, sessionId))
        await(mutesRepository.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(mutesRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(mutesRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }
  }

  

}

