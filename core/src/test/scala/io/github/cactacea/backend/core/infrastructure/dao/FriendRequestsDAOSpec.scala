package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class FriendRequestsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a friend friendRequest") {
      forOne(userGen, userGen) { (s, a1) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val friendRequestId = await(friendRequestsDAO.create(userId1, sessionId))
        val result = await(findFriendRequest(friendRequestId))
        assert(result.exists(_.id == friendRequestId))
        assert(result.exists(_.userId == userId1))
        assert(result.exists(_.by == sessionId.userId))
        assert(result.exists(!_.notified))
      }
    }
  }

  feature("delete") {

    scenario("should delete a friend friendRequest") {
      forOne(userGen, userGen) { (s, a1) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val friendRequestId = await(friendRequestsDAO.create(userId1, sessionId))
        val result1 = await(findFriendRequest(friendRequestId))
        assert(result1.isDefined)
        await(friendRequestsDAO.delete(userId1, sessionId))
        val result2 = await(findFriendRequest(friendRequestId))
        assert(result2.isEmpty)
      }
    }
  }

  feature("own") {
    scenario("should return exist or not") {
      forOne(userGen, userGen, userGen) { (s, a1, a2) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        await(friendRequestsDAO.create(userId1, sessionId))
        assertFutureValue(friendRequestsDAO.own(userId1, sessionId), true)
        assertFutureValue(friendRequestsDAO.own(userId2, sessionId), false)
      }
    }
  }

  feature("find a friendRequest") {
    scenario("should return a friendRequest") {
      forOne(userGen, userGen, userGen) { (s, a1, a2) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val friendRequestId1 = await(friendRequestsDAO.create(userId1, sessionId))
        val friendRequestId2 = await(friendRequestsDAO.create(userId2, sessionId))
        val result1 = await(friendRequestsDAO.find(friendRequestId1, userId1))
        assert(result1.exists(_ == sessionId.userId))
        val result2 = await(friendRequestsDAO.find(friendRequestId2, userId2))
        assert(result2.exists(_ == sessionId.userId))
      }

    }
  }

  feature("find requests") {

    scenario("should return received friendRequest") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        val userId5 = await(usersDAO.create(a5.userName))
        val id1 = await(friendRequestsDAO.create(sessionId.userId, userId1.sessionId))
        val id2 = await(friendRequestsDAO.create(sessionId.userId, userId2.sessionId))
        val id3 = await(friendRequestsDAO.create(sessionId.userId, userId3.sessionId))
        val id4 = await(friendRequestsDAO.create(sessionId.userId, userId4.sessionId))
        await(friendRequestsDAO.create(userId5, sessionId))

        val result1 = await(friendRequestsDAO.find(None, 0, 3, true, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).user.id == userId4)
        assert(result1(1).id == id3)
        assert(result1(1).user.id == userId3)
        assert(result1(2).id == id2)
        assert(result1(2).user.id == userId2)

        val result2 = await(friendRequestsDAO.find(result1.lastOption.map(_.next), 0, 3, true, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).user.id == userId1)
      }
    }

    scenario("should return friendRequest user send") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        val userId5 = await(usersDAO.create(a5.userName))
        val id1 = await(friendRequestsDAO.create(userId1, sessionId))
        val id2 = await(friendRequestsDAO.create(userId2, sessionId))
        val id3 = await(friendRequestsDAO.create(userId3, sessionId))
        val id4 = await(friendRequestsDAO.create(userId4, sessionId))
        await(friendRequestsDAO.create(sessionId.userId, userId5.sessionId))

        val result1 = await(friendRequestsDAO.find(None, 0, 3, false, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).user.id == userId4)
        assert(result1(1).id == id3)
        assert(result1(1).user.id == userId3)
        assert(result1(2).id == id2)
        assert(result1(2).user.id == userId2)

        val result2 = await(friendRequestsDAO.find(result1.lastOption.map(_.next), 0, 3, false, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).user.id == userId1)
      }
    }

  }


}

