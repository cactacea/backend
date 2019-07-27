package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class FriendRequestsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a friend friendRequest") {
      forOne(accountGen, accountGen) { (s, a1) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val friendRequestId = await(friendRequestsDAO.create(accountId1, sessionId))
        val result = await(findFriendRequest(friendRequestId))
        assert(result.exists(_.id == friendRequestId))
        assert(result.exists(_.accountId == accountId1))
        assert(result.exists(_.by == sessionId.toAccountId))
        assert(result.exists(!_.notified))
      }
    }
  }

  feature("delete") {

    scenario("should delete a friend friendRequest") {
      forOne(accountGen, accountGen) { (s, a1) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val friendRequestId = await(friendRequestsDAO.create(accountId1, sessionId))
        val result1 = await(findFriendRequest(friendRequestId))
        assert(result1.isDefined)
        await(friendRequestsDAO.delete(accountId1, sessionId))
        val result2 = await(findFriendRequest(friendRequestId))
        assert(result2.isEmpty)
      }
    }
  }

  feature("own") {
    scenario("should return exist or not") {
      forOne(accountGen, accountGen, accountGen) { (s, a1, a2) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        await(friendRequestsDAO.create(accountId1, sessionId))
        assertFutureValue(friendRequestsDAO.own(accountId1, sessionId), true)
        assertFutureValue(friendRequestsDAO.own(accountId2, sessionId), false)
      }
    }
  }

  feature("find a friendRequest") {
    scenario("should return a friendRequest") {
      forOne(accountGen, accountGen, accountGen) { (s, a1, a2) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val friendRequestId1 = await(friendRequestsDAO.create(accountId1, sessionId))
        val friendRequestId2 = await(friendRequestsDAO.create(accountId2, sessionId))
        val result1 = await(friendRequestsDAO.find(friendRequestId1, accountId1))
        assert(result1.exists(_ == sessionId.toAccountId))
        val result2 = await(friendRequestsDAO.find(friendRequestId2, accountId2))
        assert(result2.exists(_ == sessionId.toAccountId))
      }

    }
  }

  feature("find requests") {

    scenario("should return received friendRequest") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        val accountId5 = await(accountsDAO.create(a5.accountName))
        val id1 = await(friendRequestsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        val id2 = await(friendRequestsDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        val id3 = await(friendRequestsDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        val id4 = await(friendRequestsDAO.create(sessionId.toAccountId, accountId4.toSessionId))
        await(friendRequestsDAO.create(accountId5, sessionId))

        val result1 = await(friendRequestsDAO.find(None, 0, 3, true, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).account.id == accountId4)
        assert(result1(1).id == id3)
        assert(result1(1).account.id == accountId3)
        assert(result1(2).id == id2)
        assert(result1(2).account.id == accountId2)

        val result2 = await(friendRequestsDAO.find(result1.lastOption.map(_.next), 0, 3, true, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).account.id == accountId1)
      }
    }

    scenario("should return friendRequest account send") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        val accountId5 = await(accountsDAO.create(a5.accountName))
        val id1 = await(friendRequestsDAO.create(accountId1, sessionId))
        val id2 = await(friendRequestsDAO.create(accountId2, sessionId))
        val id3 = await(friendRequestsDAO.create(accountId3, sessionId))
        val id4 = await(friendRequestsDAO.create(accountId4, sessionId))
        await(friendRequestsDAO.create(sessionId.toAccountId, accountId5.toSessionId))

        val result1 = await(friendRequestsDAO.find(None, 0, 3, false, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).account.id == accountId4)
        assert(result1(1).id == id3)
        assert(result1(1).account.id == accountId3)
        assert(result1(2).id == id2)
        assert(result1(2).account.id == accountId2)

        val result2 = await(friendRequestsDAO.find(result1.lastOption.map(_.next), 0, 3, false, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).account.id == accountId1)
      }
    }

  }


}

