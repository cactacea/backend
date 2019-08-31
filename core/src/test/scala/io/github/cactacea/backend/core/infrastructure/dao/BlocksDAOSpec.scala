package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class BlocksDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should block an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        await(blocksDAO.create(accountId2, accountId3.toSessionId))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(blocksDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(blocksDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(blocksDAO.own(accountId2, accountId3.toSessionId), true)
      }
    }
    scenario("should return an exception occurs when create duplicate block") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        // exception occurs
        assert(intercept[ServerError] {
          await(blocksDAO.create(accountId1, accountId2.toSessionId))
        }.code == 1062)
      }
    }
  }

  feature("delete") {
    scenario("should unblock an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(blocksDAO.create(accountId2, accountId1.toSessionId))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        await(blocksDAO.delete(accountId2, accountId1.toSessionId))
        await(blocksDAO.delete(accountId3, accountId1.toSessionId))
        await(blocksDAO.delete(accountId1, accountId2.toSessionId))
        assertFutureValue(blocksDAO.own(accountId3, accountId1.toSessionId), false)
        assertFutureValue(blocksDAO.own(accountId1, accountId2.toSessionId), false)
        assertFutureValue(blocksDAO.own(accountId2, accountId3.toSessionId), false)
      }
    }
  }

  feature("own") {
    scenario("should return blocked or not") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        await(blocksDAO.create(accountId2, accountId3.toSessionId))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(blocksDAO.own(accountId2, accountId1.toSessionId), false)
        assertFutureValue(blocksDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(blocksDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(blocksDAO.own(accountId3, accountId2.toSessionId), false)
        assertFutureValue(blocksDAO.own(accountId1, accountId3.toSessionId), false)
        assertFutureValue(blocksDAO.own(accountId2, accountId3.toSessionId), true)
      }
    }
  }


  feature("find") {
    scenario("should return block list in order of creation") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account block account1
        //   session account block account2
        //   session account block account3
        //   session account block account4
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(blocksDAO.create(accountId1, sessionId))
        await(blocksDAO.create(accountId2, sessionId))
        await(blocksDAO.create(accountId3, sessionId))
        await(blocksDAO.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(blocksDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(blocksDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }
  }

}

