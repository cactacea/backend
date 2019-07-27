package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class FollowsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should follow an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(accountId1, accountId2.toSessionId))
        await(followsDAO.create(accountId2, accountId3.toSessionId))
        await(followsDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(followsDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(followsDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(followsDAO.own(accountId2, accountId3.toSessionId), true)
        val result1 = await(accountsDAO.find(accountId1.toSessionId))
        val result2 = await(accountsDAO.find(accountId2.toSessionId))
        val result3 = await(accountsDAO.find(accountId3.toSessionId))
        assert(result1.map(_.followCount) == Option(1L))
        assert(result2.map(_.followCount) == Option(1L))
        assert(result3.map(_.followCount) == Option(1L))
      }
    }

    scenario("should return an exception occurs when create duplicate follow") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        await(followsDAO.create(accountId1, accountId2.toSessionId))
        // exception occurs
        assert(intercept[ServerError] {
          await(followsDAO.create(accountId1, accountId2.toSessionId))
        }.code == 1062)

      }
    }

  }

  feature("delete") {
    scenario("should unfollow an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(accountId1, accountId2.toSessionId))
        await(followsDAO.create(accountId2, accountId3.toSessionId))
        await(followsDAO.create(accountId3, accountId1.toSessionId))
        await(followsDAO.delete(accountId1, accountId2.toSessionId))
        await(followsDAO.delete(accountId2, accountId3.toSessionId))
        await(followsDAO.delete(accountId3, accountId1.toSessionId))
        assertFutureValue(followsDAO.own(accountId1, accountId2.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId2, accountId3.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId3, accountId1.toSessionId), false)
        val result1 = await(accountsDAO.find(accountId1.toSessionId))
        val result2 = await(accountsDAO.find(accountId2.toSessionId))
        val result3 = await(accountsDAO.find(accountId3.toSessionId))
        assert(result1.map(_.followCount) == Option(0L))
        assert(result2.map(_.followCount) == Option(0L))
        assert(result3.map(_.followCount) == Option(0L))
      }
    }
  }

  feature("own") {
    scenario("should return followed or not") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(accountId1, accountId2.toSessionId))
        await(followsDAO.create(accountId2, accountId3.toSessionId))
        await(followsDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(followsDAO.own(accountId2, accountId1.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(followsDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(followsDAO.own(accountId3, accountId2.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId1, accountId3.toSessionId), false)
        assertFutureValue(followsDAO.own(accountId2, accountId3.toSessionId), true)
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
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followsDAO.create(accountId1, sessionId))
        await(followsDAO.create(accountId2, sessionId))
        await(followsDAO.create(accountId3, sessionId))
        await(followsDAO.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followsDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(followsDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
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
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followsDAO.create(accountId1, sessionId))
        await(followsDAO.create(accountId2, sessionId))
        await(followsDAO.create(accountId3, sessionId))
        await(followsDAO.create(accountId4, sessionId))

        // account2 block account1
        await(blocksDAO.create(accountId1, accountId2.toSessionId))

        // return account1 found
        // return account2 not found because of account2 be blocked by account1
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followsDAO.find(sessionId.toAccountId, Option(h), None, 0, 2, accountId1.toSessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId1)

        val result2 = await(followsDAO.find(sessionId.toAccountId, Option(h), result1.lastOption.map(_.next), 0, 2, accountId1.toSessionId))
        assert(result2.size == 0)
      }
    }

  }
  

}

