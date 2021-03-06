package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class BlocksDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should block an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(blocksDAO.create(userId1, userId2.sessionId))
        await(blocksDAO.create(userId2, userId3.sessionId))
        await(blocksDAO.create(userId3, userId1.sessionId))
        assertFutureValue(blocksDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(blocksDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(blocksDAO.own(userId2, userId3.sessionId), true)
      }
    }
    scenario("should return an exception occurs when create duplicate block") {
      forOne(userGen, userGen) { (a1, a2) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        await(blocksDAO.create(userId1, userId2.sessionId))
        // exception occurs
        assert(intercept[ServerError] {
          await(blocksDAO.create(userId1, userId2.sessionId))
        }.code == 1062)
      }
    }
  }

  feature("delete") {
    scenario("should unblock an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(blocksDAO.create(userId2, userId1.sessionId))
        await(blocksDAO.create(userId3, userId1.sessionId))
        await(blocksDAO.create(userId1, userId2.sessionId))
        await(blocksDAO.delete(userId2, userId1.sessionId))
        await(blocksDAO.delete(userId3, userId1.sessionId))
        await(blocksDAO.delete(userId1, userId2.sessionId))
        assertFutureValue(blocksDAO.own(userId3, userId1.sessionId), false)
        assertFutureValue(blocksDAO.own(userId1, userId2.sessionId), false)
        assertFutureValue(blocksDAO.own(userId2, userId3.sessionId), false)
      }
    }
  }

  feature("own") {
    scenario("should return blocked or not") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(blocksDAO.create(userId1, userId2.sessionId))
        await(blocksDAO.create(userId2, userId3.sessionId))
        await(blocksDAO.create(userId3, userId1.sessionId))
        assertFutureValue(blocksDAO.own(userId2, userId1.sessionId), false)
        assertFutureValue(blocksDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(blocksDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(blocksDAO.own(userId3, userId2.sessionId), false)
        assertFutureValue(blocksDAO.own(userId1, userId3.sessionId), false)
        assertFutureValue(blocksDAO.own(userId2, userId3.sessionId), true)
      }
    }
  }


  feature("find") {
    scenario("should return block list in order of creation") {
      forAll(sortedNameGen, userGen, sortedUserGen, sortedUserGen, sortedUserGen, userGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session user block user1
        //   session user block user2
        //   session user block user3
        //   session user block user4
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(h + a1.userName))
        val userId2 = await(usersDAO.create(h + a2.userName))
        val userId3 = await(usersDAO.create(h + a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(blocksDAO.create(userId1, sessionId))
        await(blocksDAO.create(userId2, sessionId))
        await(blocksDAO.create(userId3, sessionId))
        await(blocksDAO.create(userId4, sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(blocksDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(blocksDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId1)
      }
    }
  }

}

