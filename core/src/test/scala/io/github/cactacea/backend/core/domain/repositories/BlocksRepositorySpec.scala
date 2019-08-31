package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyBlocked, AccountNotBlocked, AccountNotFound, InvalidAccountIdError}

class BlocksRepositorySpec extends RepositorySpec {

  feature("find") {
    scenario("should return blocked account list") {
      forOne(accountGen, accounts20ListGen) { (s, l) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val accounts = l.map({ a1 =>
          val a = await(accountsRepository.create(a1.accountName))
          await(blocksRepository.create(a.id, sessionId))
          a
        }).reverse

        // result
        val result = await(blocksRepository.find(None, None, 0, accounts.size, sessionId))
        result.zipWithIndex.map({ case (a, i) =>
          assert(a.id == accounts(i).id)
        })

      }
    }
  }

  feature("create") {
    scenario("should block an account") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        await(blocksRepository.create(accountId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(accountId1, sessionId), true)

      }
    }

    scenario("should delete follow and being followed") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        await(followsRepository.create(accountId1, sessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        await(blocksRepository.create(accountId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(accountId1, sessionId), true)
        assertFutureValue(followsDAO.own(accountId1, sessionId), false)
        assertFutureValue(followsDAO.own(sessionId.toAccountId, accountId1.toSessionId), false)

      }
    }

    scenario("should delete friend") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        val requestId = await(friendRequestsRepository.create(accountId1, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId1.toSessionId))
        await(blocksRepository.create(accountId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(accountId1, sessionId), true)
        assertFutureValue(friendsDAO.own(accountId1, sessionId), false)
        assertFutureValue(friendsDAO.own(sessionId.toAccountId, accountId1.toSessionId), false)

      }
    }

    scenario("should delete mutes") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        await(mutesRepository.create(accountId1, sessionId))
        await(mutesRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        await(blocksRepository.create(accountId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(accountId1, sessionId), true)
        assertFutureValue(mutesDAO.own(accountId1, sessionId), false)
        assertFutureValue(mutesDAO.own(sessionId.toAccountId, accountId1.toSessionId), false)

      }
    }

    scenario("should delete friend requests each other") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        await(friendRequestsRepository.create(accountId1, sessionId))
        await(friendRequestsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        await(blocksRepository.create(accountId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(accountId1, sessionId), true)
        assertFutureValue(friendRequestsDAO.own(accountId1, sessionId), false)
        assertFutureValue(friendRequestsDAO.own(sessionId.toAccountId, accountId1.toSessionId), false)

      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.create(sessionId.toAccountId, sessionId))
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
          await(blocksRepository.create(AccountId(0), sessionId))
        }.error == AccountNotFound)

      }
    }

    scenario("should return exception if account already blocked") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        await(blocksRepository.create(accountId1, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.create(accountId1, sessionId))
        }.error == AccountAlreadyBlocked)

      }
    }

  }

  feature("delete") {

    scenario("should unblock an account") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id
        await(blocksRepository.create(accountId1, sessionId))
        await(blocksRepository.delete(accountId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(accountId1, sessionId), false)

      }
    }

    scenario("should return exception if id is same") {
      forOne(accountGen) { (s) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.delete(sessionId.toAccountId, sessionId))
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
          await(blocksRepository.delete(AccountId(0), sessionId))
        }.error == AccountNotFound)

      }
    }

    scenario("should return exception if account already blocked") {
      forOne(accountGen, accountGen) { (s, a1) =>

        // preparing
        val session = await(accountsRepository.create(s.accountName))
        val sessionId = session.id.toSessionId
        val account1 = await(accountsRepository.create(a1.accountName))
        val accountId1 = account1.id

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.delete(accountId1, sessionId))
        }.error == AccountNotBlocked)

      }
    }

  }

}


