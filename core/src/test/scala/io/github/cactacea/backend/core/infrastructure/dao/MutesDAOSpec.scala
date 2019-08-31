package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class MutesDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should mute an block") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(mutesDAO.create(accountId1, accountId2.toSessionId))
        await(mutesDAO.create(accountId2, accountId3.toSessionId))
        await(mutesDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(mutesDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(mutesDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(mutesDAO.own(accountId2, accountId3.toSessionId), true)
      }
    }

    scenario("should return an exception occurs when create duplicate block") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        await(mutesDAO.create(accountId1, accountId2.toSessionId))
        // exception occurs
        assert(intercept[ServerError] {
          await(mutesDAO.create(accountId1, accountId2.toSessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should unmute an account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(mutesDAO.create(accountId2, accountId1.toSessionId))
        await(mutesDAO.create(accountId3, accountId1.toSessionId))
        await(mutesDAO.create(accountId1, accountId2.toSessionId))
        await(mutesDAO.delete(accountId3, accountId1.toSessionId))
        await(mutesDAO.delete(accountId1, accountId2.toSessionId))
        await(mutesDAO.delete(accountId2, accountId3.toSessionId))
        assertFutureValue(mutesDAO.own(accountId3, accountId1.toSessionId), false)
        assertFutureValue(mutesDAO.own(accountId1, accountId2.toSessionId), false)
        assertFutureValue(mutesDAO.own(accountId2, accountId3.toSessionId), false)
      }
    }
  }

  feature("own") {
    scenario("should return blocked or not") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(mutesDAO.create(accountId1, accountId2.toSessionId))
        await(mutesDAO.create(accountId2, accountId3.toSessionId))
        await(mutesDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(mutesDAO.own(accountId2, accountId1.toSessionId), false)
        assertFutureValue(mutesDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(mutesDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(mutesDAO.own(accountId3, accountId2.toSessionId), false)
        assertFutureValue(mutesDAO.own(accountId1, accountId3.toSessionId), false)
        assertFutureValue(mutesDAO.own(accountId2, accountId3.toSessionId), true)
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
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(mutesDAO.create(accountId1, sessionId))
        await(mutesDAO.create(accountId2, sessionId))
        await(mutesDAO.create(accountId3, sessionId))
        await(mutesDAO.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(mutesDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(mutesDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }
  }
}

