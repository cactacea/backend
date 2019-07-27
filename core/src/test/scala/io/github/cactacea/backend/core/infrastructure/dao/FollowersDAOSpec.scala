package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class FollowersDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create a follower account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followersDAO.create(accountId1, accountId2.toSessionId))
        await(followersDAO.create(accountId2, accountId3.toSessionId))
        await(followersDAO.create(accountId3, accountId1.toSessionId))
        val result1 = await(accountsDAO.find(accountId1.toSessionId))
        val result2 = await(accountsDAO.find(accountId2.toSessionId))
        val result3 = await(accountsDAO.find(accountId3.toSessionId))
        assert(result1.map(_.followerCount) == Option(1L))
        assert(result2.map(_.followerCount) == Option(1L))
        assert(result3.map(_.followerCount) == Option(1L))
      }
    }

    scenario("should return an exception occurs when create duplicate follower") {
      forOne(accountGen, accountGen) { (a1, a2) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        await(followersDAO.create(accountId1, accountId2.toSessionId))
        // exception occurs
        assert(intercept[ServerError] {
          await(followersDAO.create(accountId1, accountId2.toSessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should delete a follower account") {
      forAll(accountGen, accountGen, accountGen) { (a1, a2, a3) =>
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followersDAO.create(accountId1, accountId2.toSessionId))
        await(followersDAO.create(accountId2, accountId3.toSessionId))
        await(followersDAO.create(accountId3, accountId1.toSessionId))
        await(followersDAO.delete(accountId1, accountId2.toSessionId))
        await(followersDAO.delete(accountId2, accountId3.toSessionId))
        await(followersDAO.delete(accountId3, accountId1.toSessionId))
        val result1 = await(accountsDAO.find(accountId1.toSessionId))
        val result2 = await(accountsDAO.find(accountId2.toSessionId))
        val result3 = await(accountsDAO.find(accountId3.toSessionId))
        assert(result1.map(_.followerCount) == Option(0L))
        assert(result2.map(_.followerCount) == Option(0L))
        assert(result3.map(_.followerCount) == Option(0L))
      }
    }
  }

  feature("find") {

    scenario("should return session follower list") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account follower account1
        //   session account follower account2
        //   session account follower account3
        //   session account follower account4
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followersDAO.create(accountId1, sessionId))
        await(followersDAO.create(accountId2, sessionId))
        await(followersDAO.create(accountId3, sessionId))
        await(followersDAO.create(accountId4, sessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followersDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(followersDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }

    scenario("should return an account's follower list") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account follower account1
        //   session account follower account2
        //   session account follower account3
        //   session account follower account4
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(h + a1.accountName))
        val accountId2 = await(accountsDAO.create(h + a2.accountName))
        val accountId3 = await(accountsDAO.create(h + a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followersDAO.create(accountId1, sessionId))
        await(followersDAO.create(accountId2, sessionId))
        await(followersDAO.create(accountId3, sessionId))
        await(followersDAO.create(accountId4, sessionId))

        // account2 block account1
        await(blocksDAO.create(accountId1, accountId2.toSessionId))

        // return account1 found
        // return account2 not found because of account2 be blocked by account1
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followersDAO.find(sessionId.toAccountId, Option(h), None, 0, 2, accountId1.toSessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId1)

        val result2 = await(followersDAO.find(sessionId.toAccountId, Option(h), result1.lastOption.map(_.next), 0, 2, accountId1.toSessionId))
        assert(result2.size == 0)
      }
    }

  }

}

