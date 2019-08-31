package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FriendsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    forAll(accountGen, accountGen) { (a1, a2) =>
      val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
      val accountId = await(accountsDAO.create(a2.accountName))
      await(friendsDAO.create(accountId, sessionId))
      assert(await(friendsDAO.own(accountId, sessionId)))
      assert(await(accountsDAO.find(sessionId)).map(_.friendCount) == Option(1L))
      assert(await(db.run(query[Relationships]
        .filter(_.accountId == lift(accountId))
        .filter(_.by == lift(sessionId.toAccountId)).nonEmpty)))
    }

    scenario("should return an exception occurs if duplicated") {
      forAll(accountGen, accountGen) { (a1, a2) =>
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        await(friendsDAO.create(accountId, sessionId))

        // exception occurs
        assert(intercept[ServerError] {
          await(friendsDAO.create(accountId, sessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    forAll(accountGen, accountGen) { (a1, a2) =>
      val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
      val accountId = await(accountsDAO.create(a2.accountName))
      await(friendsDAO.create(accountId, sessionId))
      assert(await(friendsDAO.own(accountId, sessionId)))
      assert(await(accountsDAO.find(sessionId)).map(_.friendCount) == Option(1L))
      assert(await(db.run(query[Relationships]
        .filter(_.accountId == lift(accountId))
        .filter(_.by == lift(sessionId.toAccountId))
        .filter(_.isFriend).nonEmpty)))
      await(friendsDAO.delete(accountId, sessionId))
      assert(!await(friendsDAO.own(accountId, sessionId)))
      assert(await(accountsDAO.find(sessionId)).map(_.friendCount) == Option(0L))
      assert(await(db.run(query[Relationships]
        .filter(_.accountId == lift(accountId))
        .filter(_.by == lift(sessionId.toAccountId))
        .filter(_.isFriend).isEmpty)))
    }
  }

  feature("own") {
    scenario("should return followed or not") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(friendsDAO.create(accountId1, accountId2.toSessionId))
        await(friendsDAO.create(accountId2, accountId3.toSessionId))
        await(friendsDAO.create(accountId3, accountId1.toSessionId))
        assertFutureValue(friendsDAO.own(accountId2, accountId1.toSessionId), false)
        assertFutureValue(friendsDAO.own(accountId3, accountId1.toSessionId), true)
        assertFutureValue(friendsDAO.own(accountId1, accountId2.toSessionId), true)
        assertFutureValue(friendsDAO.own(accountId3, accountId2.toSessionId), false)
        assertFutureValue(friendsDAO.own(accountId1, accountId3.toSessionId), false)
        assertFutureValue(friendsDAO.own(accountId2, accountId3.toSessionId), true)
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
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(friendsDAO.create(accountId1, sessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(accountId3, sessionId))
        await(friendsDAO.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(friendsDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(friendsDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
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
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(friendsDAO.create(accountId1, sessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(accountId3, sessionId))
        await(friendsDAO.create(accountId4, sessionId))

        // account2 block account1
        await(blocksDAO.create(accountId1, accountId2.toSessionId))

        // return account1 found
        // return account2 not found because of account2 be blocked by account1
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(friendsDAO.find(sessionId.toAccountId, Option(h), None, 0, 2, accountId1.toSessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId1)

        val result2 = await(friendsDAO.find(sessionId.toAccountId, Option(h), result1.lastOption.map(_.next), 0, 2, accountId1.toSessionId))
        assert(result2.size == 0)
      }
    }


  }

}

