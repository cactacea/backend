package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class FollowsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should follow an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, userId2.sessionId))
        await(followsRepository.create(userId2, userId3.sessionId))
        await(followsRepository.create(userId3, userId1.sessionId))
        assertFutureValue(followsDAO.own(userId3, userId1.sessionId), true)
        assertFutureValue(followsDAO.own(userId1, userId2.sessionId), true)
        assertFutureValue(followsDAO.own(userId2, userId3.sessionId), true)
        val result1 = await(usersRepository.find(userId1.sessionId))
        val result2 = await(usersRepository.find(userId2.sessionId))
        val result3 = await(usersRepository.find(userId3.sessionId))
        assert(result1.followCount == 1L)
        assert(result2.followCount == 1L)
        assert(result3.followCount == 1L)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (a1) =>
        val userId1 = await(createUser(a1.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.create(userId1, userId1.sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen) { (a1) =>
        val userId1 = await(createUser(a1.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.create(UserId(0), userId1.sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if user already followed") {
      forOne(userGen, userGen) { (a1, a2) =>
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        await(followsRepository.create(userId1, userId2.sessionId))
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.create(userId1, userId2.sessionId))
        }.error == UserAlreadyFollowed)
      }
    }


  }

  feature("delete") {
    scenario("should unfollow an user") {
      forAll(userGen, userGen, userGen) { (a1, a2, a3) =>
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(followsRepository.create(userId1, userId2.sessionId))
        await(followsRepository.create(userId2, userId3.sessionId))
        await(followsRepository.create(userId3, userId1.sessionId))
        await(followsRepository.delete(userId1, userId2.sessionId))
        await(followsRepository.delete(userId2, userId3.sessionId))
        await(followsRepository.delete(userId3, userId1.sessionId))
        assertFutureValue(followsDAO.own(userId1, userId2.sessionId), false)
        assertFutureValue(followsDAO.own(userId2, userId3.sessionId), false)
        assertFutureValue(followsDAO.own(userId3, userId1.sessionId), false)
        val result1 = await(usersRepository.find(userId1.sessionId))
        val result2 = await(usersRepository.find(userId2.sessionId))
        val result3 = await(usersRepository.find(userId3.sessionId))
        assert(result1.followCount == 0L)
        assert(result2.followCount == 0L)
        assert(result3.followCount == 0L)
      }
    }

    scenario("should return exception if id is same") {
      forOne(userGen) { (a1) =>
        val userId1 = await(createUser(a1.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.delete(userId1, userId1.sessionId))
        }.error == InvalidUserIdError)
      }
    }

    scenario("should return exception if user not exist") {
      forOne(userGen) { (a1) =>
        val userId1 = await(createUser(a1.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.delete(UserId(0), userId1.sessionId))
        }.error == UserNotFound)
      }
    }

    scenario("should return exception if user not followed") {
      forOne(userGen, userGen) { (a1, a2) =>
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.delete(userId1, userId2.sessionId))
        }.error == UserNotFollowed)
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
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(h + a1.userName)).id
        val userId2 = await(createUser(h + a2.userName)).id
        val userId3 = await(createUser(h + a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        await(followsRepository.create(userId1, sessionId))
        await(followsRepository.create(userId2, sessionId))
        await(followsRepository.create(userId3, sessionId))
        await(followsRepository.create(userId4, sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(followsRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(followsRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
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
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(h + a1.userName)).id
        val userId2 = await(createUser(h + a2.userName)).id
        val userId3 = await(createUser(h + a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        await(followsRepository.create(userId1, sessionId))
        await(followsRepository.create(userId2, sessionId))
        await(followsRepository.create(userId3, sessionId))
        await(followsRepository.create(userId4, sessionId))

        // user2 block user1
        await(blocksRepository.create(userId1, userId2.sessionId))

        // return user1 found
        // return user2 not found because of user2 be blocked by user1
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(followsRepository.find(sessionId.userId, Option(h), None, 0, 2, userId1.sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId1)

        val result2 = await(followsRepository.find(sessionId.userId, Option(h), result1.lastOption.map(_.next), 0, 2, userId1.sessionId))
        assert(result2.size == 0)
      }
    }

  scenario("should return exception if an user not exist") {
      forOne(userGen) { (a1) =>
        val userId1 = await(createUser(a1.userName)).id
        // exception occurs
        assert(intercept[CactaceaException] {
          await(followsRepository.find(UserId(0), None, None, 0, 2, userId1.sessionId))
        }.error == UserNotFound)
      }
    }
  }

}
