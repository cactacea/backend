package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class FollowsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should follow an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(userId1, userId2.sessionId))
        await(followsDAO.create(userId2, userId3.sessionId))
        await(followsDAO.create(userId3, userId1.sessionId))
        assertFutureValue(followsDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(followsDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(followsDAO.own(userId2, userId3.sessionId), true)
        val result1 = await(usersDAO.find(userId1.sessionId))
        val result2 = await(usersDAO.find(userId2.sessionId))
        val result3 = await(usersDAO.find(userId3.sessionId))
        assert(result1.map(_.followCount) == Option(1L))
        assert(result2.map(_.followCount) == Option(1L))
        assert(result3.map(_.followCount) == Option(1L))
      }
    }

    scenario("should return an exception occurs when create duplicate follow") {
      forOne(userGen, userGen) { (a1, a2) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        await(followsDAO.create(userId1, userId2.sessionId))
        // exception occurs
        assert(intercept[ServerError] {
          await(followsDAO.create(userId1, userId2.sessionId))
        }.code == 1062)

      }
    }

  }

  feature("delete") {
    scenario("should unfollow an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(userId1, userId2.sessionId))
        await(followsDAO.create(userId2, userId3.sessionId))
        await(followsDAO.create(userId3, userId1.sessionId))
        await(followsDAO.delete(userId1, userId2.sessionId))
        await(followsDAO.delete(userId2, userId3.sessionId))
        await(followsDAO.delete(userId3, userId1.sessionId))
        assertFutureValue(followsDAO.own(userId1, userId2.sessionId), false)
        assertFutureValue(followsDAO.own(userId2, userId3.sessionId), false)
        assertFutureValue(followsDAO.own(userId3, userId1.sessionId), false)
        val result1 = await(usersDAO.find(userId1.sessionId))
        val result2 = await(usersDAO.find(userId2.sessionId))
        val result3 = await(usersDAO.find(userId3.sessionId))
        assert(result1.map(_.followCount) == Option(0L))
        assert(result2.map(_.followCount) == Option(0L))
        assert(result3.map(_.followCount) == Option(0L))
      }
    }
  }

  feature("own") {
    scenario("should return followed or not") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(userId1, userId2.sessionId))
        await(followsDAO.create(userId2, userId3.sessionId))
        await(followsDAO.create(userId3, userId1.sessionId))
        assertFutureValue(followsDAO.own(userId2, userId1.sessionId), false)
        assertFutureValue(followsDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(followsDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(followsDAO.own(userId3, userId2.sessionId), false)
        assertFutureValue(followsDAO.own(userId1, userId3.sessionId), false)
        assertFutureValue(followsDAO.own(userId2, userId3.sessionId), true)
      }
    }
  }

  feature("find") {

    scenario("should return session follow list") {
      forAll(sortedNameGen, userGen, sortedUserGen, sortedUserGen, sortedUserGen, userGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session user follow user1
        //   session user follow user2
        //   session user follow user3
        //   session user follow user4
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(h + a1.userName))
        val userId2 = await(usersDAO.create(h + a2.userName))
        val userId3 = await(usersDAO.create(h + a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(followsDAO.create(userId1, sessionId))
        await(followsDAO.create(userId2, sessionId))
        await(followsDAO.create(userId3, sessionId))
        await(followsDAO.create(userId4, sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(followsDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(followsDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId1)
      }
    }

    scenario("should return an user's follow list") {
      forAll(sortedNameGen, userGen, sortedUserGen, sortedUserGen, sortedUserGen, userGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session user follow user1
        //   session user follow user2
        //   session user follow user3
        //   session user follow user4
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(h + a1.userName))
        val userId2 = await(usersDAO.create(h + a2.userName))
        val userId3 = await(usersDAO.create(h + a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(followsDAO.create(userId1, sessionId))
        await(followsDAO.create(userId2, sessionId))
        await(followsDAO.create(userId3, sessionId))
        await(followsDAO.create(userId4, sessionId))

        // user2 block user1
        await(blocksDAO.create(userId1, userId2.sessionId))

        // return user1 found
        // return user2 not found because of user2 be blocked by user1
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(followsDAO.find(sessionId.userId, Option(h), None, 0, 2, userId1.sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId1)

        val result2 = await(followsDAO.find(sessionId.userId, Option(h), result1.lastOption.map(_.next), 0, 2, userId1.sessionId))
        assert(result2.size == 0)
      }
    }

  }
  

}

