package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec

class FollowersRepositorySpec extends RepositorySpec {


  feature("find") {

    scenario("should return session follower list") {
      forAll(sortedNameGen, userGen, sortedUserGen, sortedUserGen, sortedUserGen, userGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session user follower user1
        //   session user follower user2
        //   session user follower user3
        //   session user follower user4
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(h + a1.userName)).id
        val userId2 = await(usersRepository.create(h + a2.userName)).id
        val userId3 = await(usersRepository.create(h + a3.userName)).id
        val userId4 = await(usersRepository.create(a4.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        await(followsRepository.create(sessionId.userId, userId2.sessionId))
        await(followsRepository.create(sessionId.userId, userId3.sessionId))
        await(followsRepository.create(sessionId.userId, userId4.sessionId))

        // return user1 found
        // return user2 found
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(followersRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId2)

        val result2 = await(followersRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == userId1)
      }
    }

    scenario("should return an user's follower list") {
      forAll(sortedNameGen, userGen, sortedUserGen, sortedUserGen, sortedUserGen, userGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session user follower user1
        //   session user follower user2
        //   session user follower user3
        //   session user follower user4
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(h + a1.userName)).id
        val userId2 = await(usersRepository.create(h + a2.userName)).id
        val userId3 = await(usersRepository.create(h + a3.userName)).id
        val userId4 = await(usersRepository.create(a4.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        await(followsRepository.create(sessionId.userId, userId2.sessionId))
        await(followsRepository.create(sessionId.userId, userId3.sessionId))
        await(followsRepository.create(sessionId.userId, userId4.sessionId))

        // user2 block user1
        await(blocksRepository.create(userId1, userId2.sessionId))

        // return user1 found
        // return user2 not found because of user2 be blocked by user1
        // return user3 found
        // return user4 not found because of user name not matched
        val result1 = await(followersRepository.find(sessionId.userId, Option(h), None, 0, 2, userId1.sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == userId3)
        assert(result1(1).id == userId1)

        val result2 = await(followersRepository.find(sessionId.userId, Option(h), result1.lastOption.map(_.next), 0, 2, userId1.sessionId))
        assert(result2.size == 0)
      }
    }

  }


}
