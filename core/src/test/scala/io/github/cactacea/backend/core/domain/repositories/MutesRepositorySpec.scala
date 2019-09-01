package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyMuted, UserNotFound, UserNotMuted, InvalidUserIdError}

class MutesRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should mute an block") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersRepository.create(a1.userName)).id
        val userId2 = await(usersRepository.create(a2.userName)).id
        val userId3 = await(usersRepository.create(a3.userName)).id
        await(mutesRepository.create(userId1, userId2.sessionId))
        await(mutesRepository.create(userId2, userId3.sessionId))
        await(mutesRepository.create(userId3, userId1.sessionId))
        assertFutureValue(mutesDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(mutesDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(mutesDAO.own(userId2, userId3.sessionId), true)
      }
    }

    scenario("should return exception if id is not same") {
      forOne(userGen) { (s) =>
        val userId2 = await(usersRepository.create(s.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.create(userId2, userId2.sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen) { (s) =>
        val userId2 = await(usersRepository.create(s.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.create(UserId(0), userId2.sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if user already muted") {
      forOne(userGen, userGen) { (a1, a2) =>
        val userId1 = await(usersRepository.create(a1.userName)).id
        val userId2 = await(usersRepository.create(a2.userName)).id
        await(mutesRepository.create(userId1, userId2.sessionId))
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.create(userId1, userId2.sessionId))
        }.error == UserAlreadyMuted)
      }
    }

  }

  feature("delete") {
    scenario("should unmute an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(usersRepository.create(a1.userName)).id
        val userId2 = await(usersRepository.create(a2.userName)).id
        val userId3 = await(usersRepository.create(a3.userName)).id
        await(mutesRepository.create(userId2, userId1.sessionId))
        await(mutesRepository.create(userId3, userId1.sessionId))
        await(mutesRepository.delete(userId2, userId1.sessionId))
        await(mutesRepository.delete(userId3, userId1.sessionId))
        assertFutureValue(mutesDAO.own(userId2, userId1.sessionId), false)
        assertFutureValue(mutesDAO.own(userId3, userId1.sessionId), false)
      }
    }

    scenario("should return exception if id is not same") {
      forOne(userGen) { (s) =>
        val userId2 = await(usersRepository.create(s.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.delete(userId2, userId2.sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen) { (s) =>
        val userId2 = await(usersRepository.create(s.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.delete(UserId(0), userId2.sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if user not muted") {
      forOne(userGen, userGen) { (a1, a2) =>
        val userId1 = await(usersRepository.create(a1.userName)).id
        val userId2 = await(usersRepository.create(a2.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(mutesRepository.delete(userId1, userId2.sessionId))
        }.error == UserNotMuted)
      }
    }

  }

  feature("find") {
    scenario("should return mute list in order of creation") {
      forAll(sortedNameGen, userGen, sortedUserGen, sortedUserGen, sortedUserGen, userGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session user mute user1
        //   session user mute user2
        //   session user mute user3
        //   session user mute user4
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(h + a1.userName)).id
        val userId2 = await(usersRepository.create(h + a2.userName)).id
        val userId3 = await(usersRepository.create(h + a3.userName)).id
        val userId4 = await(usersRepository.create(a4.userName)).id
        await(mutesRepository.create(userId1, sessionId))
        await(mutesRepository.create(userId2, sessionId))
        await(mutesRepository.create(userId3, sessionId))
        await(mutesRepository.create(userId4, sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(mutesRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(mutesRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId1)
      }
    }
  }

  

}

