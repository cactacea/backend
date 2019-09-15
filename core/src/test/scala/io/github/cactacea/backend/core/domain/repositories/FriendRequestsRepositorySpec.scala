package io.github.cactacea.backend.core.domain.repositories

import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.FeedType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{FriendRequestId, UserId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class FriendRequestsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a friend request") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val friendRequestId = await(friendRequestsRepository.create(userId1, sessionId))

        // result
        val result1 = await(friendRequestsRepository.find(None, 0, 10, true, userId1.sessionId))
        assert(result1.headOption.exists(_.id == friendRequestId))
        assert(result1.headOption.exists(_.user.id == sessionId.userId))

        val result2 = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), userId1.sessionId))
        assert(result2.headOption.exists(_.contentId.exists(_ == friendRequestId.value)))
        assert(result2.headOption.exists(_.feedType == FeedType.friendRequest))
      }
    }


    scenario("should return exception if id is same") {
      forOne(userGen) { (s) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.create(sessionId.userId, sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen) { (s) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.create(UserId(0), sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if already requested") {
      forOne(userGen, userGen) { (s, a1) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        await(friendRequestsRepository.create(userId1, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.create(userId1, sessionId))
        }.error == UserAlreadyRequested)
      }
    }

  }


  feature("delete") {
    
    scenario("should delete a friend request") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val friendRequestId = await(friendRequestsRepository.create(userId1, sessionId))

        // result
        val result1 = await(findFriendRequest(friendRequestId))
        assert(result1.isDefined)
        await(friendRequestsRepository.delete(userId1, sessionId))
        val result2 = await(findFriendRequest(friendRequestId))
        assert(result2.isEmpty)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (s) =>
        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.delete(sessionId.userId, sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen) { (s) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.delete(UserId(0), sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if not requested") {
      forOne(userGen, userGen) { (s, a1) =>
        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.delete(userId1, sessionId))
        }.error == FriendRequestNotFound)
      }
    }

  }


  feature("find requests") {

    scenario("should return received friendRequest") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        val userId5 = await(createUser(a5.userName)).id
        val id1 = await(friendRequestsRepository.create(sessionId.userId, userId1.sessionId))
        val id2 = await(friendRequestsRepository.create(sessionId.userId, userId2.sessionId))
        val id3 = await(friendRequestsRepository.create(sessionId.userId, userId3.sessionId))
        val id4 = await(friendRequestsRepository.create(sessionId.userId, userId4.sessionId))
        await(friendRequestsRepository.create(userId5, sessionId))

        val result1 = await(friendRequestsRepository.find(None, 0, 3, true, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).user.id == userId4)
        assert(result1(1).id == id3)
        assert(result1(1).user.id == userId3)
        assert(result1(2).id == id2)
        assert(result1(2).user.id == userId2)

        val result2 = await(friendRequestsRepository.find(result1.lastOption.map(_.next), 0, 3, true, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).user.id == userId1)
      }
    }

    scenario("should return friendRequest user send") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen) { (s, a1, a2, a3, a4, a5) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        val userId5 = await(createUser(a5.userName)).id
        val id1 = await(friendRequestsRepository.create(userId1, sessionId))
        val id2 = await(friendRequestsRepository.create(userId2, sessionId))
        val id3 = await(friendRequestsRepository.create(userId3, sessionId))
        val id4 = await(friendRequestsRepository.create(userId4, sessionId))
        await(friendRequestsRepository.create(sessionId.userId, userId5.sessionId))

        val result1 = await(friendRequestsRepository.find(None, 0, 3, false, sessionId))
        assert(result1.size == 3)
        assert(result1(0).id == id4)
        assert(result1(0).user.id == userId4)
        assert(result1(1).id == id3)
        assert(result1(1).user.id == userId3)
        assert(result1(2).id == id2)
        assert(result1(2).user.id == userId2)

        val result2 = await(friendRequestsRepository.find(result1.lastOption.map(_.next), 0, 3, false, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == id1)
        assert(result2(0).user.id == userId1)
      }
    }

  }

  feature("accept") {
    scenario("should delete a friendRequest") {
      forOne(userGen, userGen) { (s, a1) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val friendRequestId = await(friendRequestsRepository.create(userId1, sessionId))
        await(friendRequestsRepository.accept(friendRequestId, userId1.sessionId))

        // result
        val result = await(friendsRepository.find(None, None, 0, 10, userId1.sessionId))
        assert(result.headOption.exists(_.id == sessionId.userId))
        assertFutureValue(friendRequestsDAO.own(userId1, sessionId), false)
      }
    }

    scenario("should return exception if friendRequest not exist") {
      forOne(userGen) { (s) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.accept(FriendRequestId(0), sessionId))
        }.error == FriendRequestNotFound)
      }
    }

  }

  feature("reject") {
    scenario("should delete a friend request") {
      forOne(userGen, userGen) { (s, a1) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val friendRequestId = await(friendRequestsRepository.create(userId1, sessionId))
        await(friendRequestsRepository.reject(friendRequestId, userId1.sessionId))

        // result
        assertFutureValue(friendRequestsDAO.own(userId1, sessionId), false)
      }
    }
    scenario("should return exception if friendRequest not exist") {
      forOne(userGen) { (s) =>
        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendRequestsRepository.reject(FriendRequestId(0), sessionId))
        }.error == FriendRequestNotFound)
      }
    }
  }


}

