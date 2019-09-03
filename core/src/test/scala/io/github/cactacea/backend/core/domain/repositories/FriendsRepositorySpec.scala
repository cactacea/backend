package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserNotFound, InvalidUserIdError}

class FriendsRepositorySpec extends RepositorySpec {

  feature("delete") {

    scenario("should delete a friend") {
      forAll(userGen, userGen) { (a1, a2) =>
        val sessionId = await(createUser(a1.userName)).id.sessionId
        val userId = await(createUser(a2.userName)).id
        val requestId = await(friendRequestsRepository.create(userId, sessionId))
        await(friendRequestsRepository.accept(requestId, userId.sessionId))

        assert(await(friendsDAO.own(userId, sessionId)))
        assert(await(friendsDAO.own(sessionId.userId, userId.sessionId)))
        assert(await(usersRepository.find(sessionId)).friendCount == 1L)
        assert(await(usersRepository.find(userId.sessionId)).friendCount == 1L)
        await(friendsRepository.delete(userId, sessionId))

        assert(!await(friendsDAO.own(userId, sessionId)))
        assert(!await(friendsDAO.own(sessionId.userId, userId.sessionId)))
        assert(await(usersRepository.find(sessionId)).friendCount == 0L)
        assert(await(usersRepository.find(userId.sessionId)).friendCount == 0L)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.delete(sessionId.userId, sessionId))
        }.error == InvalidUserIdError)

      }
    }

    scenario("should return exception if user is not exist") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.delete(UserId(0), sessionId))
        }.error == UserNotFound)

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
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(h + a1.userName)).id
        val userId2 = await(createUser(h + a2.userName)).id
        val userId3 = await(createUser(h + a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        val requestId1 = await(friendRequestsRepository.create(userId1, sessionId))
        val requestId2 = await(friendRequestsRepository.create(userId2, sessionId))
        val requestId3 = await(friendRequestsRepository.create(userId3, sessionId))
        val requestId4 = await(friendRequestsRepository.create(userId4, sessionId))
        await(friendRequestsRepository.accept(requestId1, userId1.sessionId))
        await(friendRequestsRepository.accept(requestId2, userId2.sessionId))
        await(friendRequestsRepository.accept(requestId3, userId3.sessionId))
        await(friendRequestsRepository.accept(requestId4, userId4.sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(friendsRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(friendsRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
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
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(h + a1.userName)).id
        val userId2 = await(createUser(h + a2.userName)).id
        val userId3 = await(createUser(h + a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        val requestId1 = await(friendRequestsRepository.create(userId1, sessionId))
        val requestId2 = await(friendRequestsRepository.create(userId2, sessionId))
        val requestId3 = await(friendRequestsRepository.create(userId3, sessionId))
        val requestId4 = await(friendRequestsRepository.create(userId4, sessionId))
        await(friendRequestsRepository.accept(requestId1, userId1.sessionId))
        await(friendRequestsRepository.accept(requestId2, userId2.sessionId))
        await(friendRequestsRepository.accept(requestId3, userId3.sessionId))
        await(friendRequestsRepository.accept(requestId4, userId4.sessionId))

        // user2 block user1
        await(blocksRepository.create(userId1, userId2.sessionId))

        // return user1 found
        // return user2 not found because of user2 be blocked by user1
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(friendsRepository.find(sessionId.userId, Option(h), None, 0, 2, userId1.sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId1)

        val result2 = await(friendsRepository.find(sessionId.userId, Option(h), result1.lastOption.map(_.next), 0, 2, userId1.sessionId))
        assert(result2.size == 0)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.find(sessionId.userId, None, None, 0, 2, sessionId))
        }.error == InvalidUserIdError)

      }
    }

    scenario("should return exception if user is not exist") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(friendsRepository.find(UserId(0), None, None, 0, 2, sessionId))
        }.error == UserNotFound)

      }
    }

  }


}
