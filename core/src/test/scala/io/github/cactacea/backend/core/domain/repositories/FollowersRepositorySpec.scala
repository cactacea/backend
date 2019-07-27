package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.helpers.specs.RepositorySpec

class FollowersRepositorySpec extends RepositorySpec {


  feature("find") {

    scenario("should return session follower list") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account follower account1
        //   session account follower account2
        //   session account follower account3
        //   session account follower account4
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(h + a1.accountName)).id
        val accountId2 = await(accountsRepository.create(h + a2.accountName)).id
        val accountId3 = await(accountsRepository.create(h + a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId2.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId3.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId4.toSessionId))

        // return account1 found
        // return account2 found
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followersRepository.find(Option(h), None, 0, 2, sessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId2)

        val result2 = await(followersRepository.find(Option(h), result1.lastOption.map(_.next), 0, 2, sessionId))
        assert(result2.size == 1)
        assert(result2(0).id == accountId1)
      }
    }

    scenario("should return an account's follower list") {
      forAll(sortedNameGen, accountGen, sortedAccountGen, sortedAccountGen, sortedAccountGen, accountGen)
      { (h, s, a1, a2, a3, a4) =>

        // preparing
        //   session account follower account1
        //   session account follower account2
        //   session account follower account3
        //   session account follower account4
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(h + a1.accountName)).id
        val accountId2 = await(accountsRepository.create(h + a2.accountName)).id
        val accountId3 = await(accountsRepository.create(h + a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId2.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId3.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId4.toSessionId))

        // account2 block account1
        await(blocksRepository.create(accountId1, accountId2.toSessionId))

        // return account1 found
        // return account2 not found because of account2 be blocked by account1
        // return account3 found
        // return account4 not found because of account name not matched
        val result1 = await(followersRepository.find(sessionId.toAccountId, Option(h), None, 0, 2, accountId1.toSessionId))
        assert(result1.size == 2)
        assert(result1(0).id == accountId3)
        assert(result1(1).id == accountId1)

        val result2 = await(followersRepository.find(sessionId.toAccountId, Option(h), result1.lastOption.map(_.next), 0, 2, accountId1.toSessionId))
        assert(result2.size == 0)
      }
    }

  }


}
