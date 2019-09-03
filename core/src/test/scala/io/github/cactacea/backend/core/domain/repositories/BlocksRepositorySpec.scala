package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyBlocked, UserNotBlocked, UserNotFound, InvalidUserIdError}

class BlocksRepositorySpec extends RepositorySpec {

  feature("find") {
    scenario("should return blocked user list") {
      forOne(userGen, user20ListGen) { (s, l) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val users = l.map({ a1 =>
          val a = await(createUser(a1.userName))
          await(blocksRepository.create(a.id, sessionId))
          a
        }).reverse

        // result
        val result = await(blocksRepository.find(None, None, 0, users.size, sessionId))
        result.zipWithIndex.map({ case (a, i) =>
          assert(a.id == users(i).id)
        })

      }
    }
  }

  feature("create") {
    scenario("should block an user") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        await(blocksRepository.create(userId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(userId1, sessionId), true)

      }
    }

    scenario("should delete follow and being followed") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        await(followsRepository.create(userId1, sessionId))
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        await(blocksRepository.create(userId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(userId1, sessionId), true)
        assertFutureValue(followsDAO.own(userId1, sessionId), false)
        assertFutureValue(followsDAO.own(sessionId.userId, userId1.sessionId), false)

      }
    }

    scenario("should delete friend") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        val requestId = await(friendRequestsRepository.create(userId1, sessionId))
        await(friendRequestsRepository.accept(requestId, userId1.sessionId))
        await(blocksRepository.create(userId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(userId1, sessionId), true)
        assertFutureValue(friendsDAO.own(userId1, sessionId), false)
        assertFutureValue(friendsDAO.own(sessionId.userId, userId1.sessionId), false)

      }
    }

    scenario("should delete mutes") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        await(mutesRepository.create(userId1, sessionId))
        await(mutesRepository.create(sessionId.userId, userId1.sessionId))
        await(blocksRepository.create(userId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(userId1, sessionId), true)
        assertFutureValue(mutesDAO.own(userId1, sessionId), false)
        assertFutureValue(mutesDAO.own(sessionId.userId, userId1.sessionId), false)

      }
    }

    scenario("should delete friend requests each other") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        await(friendRequestsRepository.create(userId1, sessionId))
        await(friendRequestsRepository.create(sessionId.userId, userId1.sessionId))
        await(blocksRepository.create(userId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(userId1, sessionId), true)
        assertFutureValue(friendRequestsDAO.own(userId1, sessionId), false)
        assertFutureValue(friendRequestsDAO.own(sessionId.userId, userId1.sessionId), false)

      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.create(sessionId.userId, sessionId))
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
          await(blocksRepository.create(UserId(0), sessionId))
        }.error == UserNotFound)

      }
    }

    scenario("should return exception if user already blocked") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        await(blocksRepository.create(userId1, sessionId))

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.create(userId1, sessionId))
        }.error == UserAlreadyBlocked)

      }
    }

  }

  feature("delete") {

    scenario("should unblock an user") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id
        await(blocksRepository.create(userId1, sessionId))
        await(blocksRepository.delete(userId1, sessionId))

        // result
        assertFutureValue(blocksDAO.own(userId1, sessionId), false)

      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (s) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.delete(sessionId.userId, sessionId))
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
          await(blocksRepository.delete(UserId(0), sessionId))
        }.error == UserNotFound)

      }
    }

    scenario("should return exception if user already blocked") {
      forOne(userGen, userGen) { (s, a1) =>

        // preparing
        val session = await(createUser(s.userName))
        val sessionId = session.id.sessionId
        val user1 = await(createUser(a1.userName))
        val userId1 = user1.id

        // result
        assert(intercept[CactaceaException] {
          await(blocksRepository.delete(userId1, sessionId))
        }.error == UserNotBlocked)

      }
    }

  }

}


