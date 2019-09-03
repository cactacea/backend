package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Relationships

class FriendsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    forAll(userGen, userGen) { (a1, a2) =>
      val sessionId = await(usersDAO.create(a1.userName)).sessionId
      val userId = await(usersDAO.create(a2.userName))
      await(friendsDAO.create(userId, sessionId))
      assert(await(friendsDAO.own(userId, sessionId)))
      assert(await(usersDAO.find(sessionId)).map(_.friendCount) == Option(1L))
      assert(await(db.run(query[Relationships]
        .filter(_.userId == lift(userId))
        .filter(_.by == lift(sessionId.userId)).nonEmpty)))
    }

    scenario("should return an exception occurs if duplicated") {
      forAll(userGen, userGen) { (a1, a2) =>
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        await(friendsDAO.create(userId, sessionId))

        // exception occurs
        assert(intercept[ServerError] {
          await(friendsDAO.create(userId, sessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    forAll(userGen, userGen) { (a1, a2) =>
      val sessionId = await(usersDAO.create(a1.userName)).sessionId
      val userId = await(usersDAO.create(a2.userName))
      await(friendsDAO.create(userId, sessionId))
      assert(await(friendsDAO.own(userId, sessionId)))
      assert(await(usersDAO.find(sessionId)).map(_.friendCount) == Option(1L))
      assert(await(db.run(query[Relationships]
        .filter(_.userId == lift(userId))
        .filter(_.by == lift(sessionId.userId))
        .filter(_.isFriend).nonEmpty)))
      await(friendsDAO.delete(userId, sessionId))
      assert(!await(friendsDAO.own(userId, sessionId)))
      assert(await(usersDAO.find(sessionId)).map(_.friendCount) == Option(0L))
      assert(await(db.run(query[Relationships]
        .filter(_.userId == lift(userId))
        .filter(_.by == lift(sessionId.userId))
        .filter(_.isFriend).isEmpty)))
    }
  }

  feature("own") {
    scenario("should return followed or not") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(friendsDAO.create(userId1, userId2.sessionId))
        await(friendsDAO.create(userId2, userId3.sessionId))
        await(friendsDAO.create(userId3, userId1.sessionId))
        assertFutureValue(friendsDAO.own(userId2, userId1.sessionId), false)
        assertFutureValue(friendsDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(friendsDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(friendsDAO.own(userId3, userId2.sessionId), false)
        assertFutureValue(friendsDAO.own(userId1, userId3.sessionId), false)
        assertFutureValue(friendsDAO.own(userId2, userId3.sessionId), true)
      }
    }
  }

  feature("find") {

    scenario("should return session friend list") {
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
        await(friendsDAO.create(userId1, sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(userId3, sessionId))
        await(friendsDAO.create(userId4, sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(friendsDAO.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(friendsDAO.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId1)
      }
    }

    scenario("should return an user's friend list") {
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
        await(friendsDAO.create(userId1, sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(userId3, sessionId))
        await(friendsDAO.create(userId4, sessionId))

        // user2 block user1
        await(blocksDAO.create(userId1, userId2.sessionId))

        // return user1 found
        // return user2 not found because of user2 be blocked by user1
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(friendsDAO.find(sessionId.userId, Option(h), None, 0, 2, userId1.sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId1)

        val result2 = await(friendsDAO.find(sessionId.userId, Option(h), result1.lastOption.map(_.next), 0, 2, userId1.sessionId))
        assert(result2.size == 0)
      }
    }


  }

}

