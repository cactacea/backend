package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class FeedLikesRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a feed like") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  session account create a feed
        //  account1 like a feed
        //  account2 like a feed
        //  account3 like a feed
        //  account1 block account2
        //  account2 block account3
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(blocksRepository.create(accountId2, accountId1.toSessionId))
        await(blocksRepository.create(accountId1, accountId2.toSessionId))
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(feedLikesRepository.create(feedId, accountId1.toSessionId))
        await(feedLikesRepository.create(feedId, accountId2.toSessionId))
        await(feedLikesRepository.create(feedId, accountId3.toSessionId))

        // should like count 3
        val result1 = await(feedsDAO.find(feedId, sessionId))
        assert(result1.map(_.likeCount) == Option(3))

        // should like count 2 because account1 blocked account2
        val result2 = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(result2.map(_.likeCount) == Option(2))

        // should like count 2 because account2 blocked account1
        val result3 = await(feedsDAO.find(feedId, accountId2.toSessionId))
        assert(result3.map(_.likeCount) == Option(2))
      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(accountGen) { (a1) =>

        // preparing
        val accountId1 = await(accountsRepository.create(a1.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.create(FeedId(0), accountId1.toSessionId))
        }.error == FeedNotFound)
      }
    }

    scenario("should return exception if a feed already liked") {
      forOne(accountGen, accountGen, feedGen) { (s, a1, f) =>
        // preparing
        //  session account create a feed
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        await(feedLikesRepository.create(feedId, accountId1.toSessionId))
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.create(feedId, accountId1.toSessionId))
        }.error == FeedAlreadyLiked)
      }
    }
  }

  feature("delete") {

    scenario("should delete a feed like") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session account creates a feed
          //  account1 like a feed
          //  account2 like a feed
          //  account3 like a feed
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val accountId2 = await(accountsRepository.create(a2.accountName)).id
          val accountId3 = await(accountsRepository.create(a3.accountName)).id
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesRepository.create(feedId, accountId1.toSessionId))
          await(feedLikesRepository.create(feedId, accountId2.toSessionId))
          await(feedLikesRepository.create(feedId, accountId3.toSessionId))
          await(feedLikesRepository.delete(feedId, accountId1.toSessionId))
          await(feedLikesRepository.delete(feedId, accountId2.toSessionId))
          await(feedLikesRepository.delete(feedId, accountId3.toSessionId))

          val result1 = await(feedsDAO.find(feedId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(feedsDAO.find(feedId, accountId1.toSessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(feedsDAO.find(feedId, accountId2.toSessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(accountGen) { (a1) =>

        // preparing
        val accountId1 = await(accountsRepository.create(a1.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.delete(FeedId(0), accountId1.toSessionId))
        }.error == FeedNotFound)
      }
    }

    scenario("should return exception if a feed not liked") {
      forOne(accountGen, accountGen, feedGen) { (s, a1, f) =>
        // preparing
        //  session account create a feed
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.delete(feedId, accountId1.toSessionId))
        }.error == FeedNotLiked)
      }
    }

  }

  feature("find - feeds an account liked") {

    scenario("should return feed list an account liked") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        //  account4 is a follower.
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        val accountId4 = await(accountsRepository.create(a4.accountName)).id
        await(followsRepository.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followsRepository.create(sessionId.toAccountId, accountId4.toSessionId))
        val requestId = await(friendRequestsRepository.create(accountId2, sessionId))
        await(friendRequestsRepository.accept(requestId, accountId2.toSessionId))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.followers, f.contentWarning, None, sessionId))
          await(feedLikesRepository.create(feedId, accountId4.toSessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedLikesRepository.find(accountId4, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // friend
        val result2 = await(feedLikesRepository.find(accountId4, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(feedLikesRepository.find(accountId4, None, 0, 10, accountId3.toSessionId))
        assert(result3.size == 0)

      }
    }
    
    scenario("should return exception if an account not exist") {
      forOne(accountGen) { (a1) =>

        // preparing
        val accountId1 = await(accountsRepository.create(a1.accountName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.find(AccountId(0), None, 0, 10, accountId1.toSessionId))
        }.error == AccountNotFound)
      }
    }

  }


  feature("find - feeds session account liked") {
    scenario("should return feed list session account liked") {

      forOne(accountGen, accountGen, feed20ListGen) { (s, a1, f) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, None, sessionId))
          await(feedLikesRepository.create(feedId, accountId1.toSessionId))
          f.copy(id = feedId)
        }).reverse


        val result1 = await(feedLikesRepository.find(None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

      }


    }
  }


  feature("findAccounts") {
    scenario("should return account list who liked a feed") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen, feedGen) {
        (s, a1, a2, a3, a4, a5, f) =>

          // preparing
          //  session account creates a feed
          //  account1 like a feed
          //  account2 like a feed
          //  account3 like a feed
          //  account4 like a feed
          //  account5 like a feed
          //  account4 block account5
          //  account5 block account4
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val accountId2 = await(accountsRepository.create(a2.accountName)).id
          val accountId3 = await(accountsRepository.create(a3.accountName)).id
          val accountId4 = await(accountsRepository.create(a4.accountName)).id
          val accountId5 = await(accountsRepository.create(a5.accountName)).id
          await(blocksRepository.create(accountId4, accountId5.toSessionId))
          await(blocksRepository.create(accountId5, accountId4.toSessionId))

          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesRepository.create(feedId, accountId1.toSessionId))
          await(feedLikesRepository.create(feedId, accountId2.toSessionId))
          await(feedLikesRepository.create(feedId, accountId3.toSessionId))
          await(feedLikesRepository.create(feedId, accountId4.toSessionId))
          await(feedLikesRepository.create(feedId, accountId5.toSessionId))

          // should return account list
          val result1 = await(feedLikesRepository.findAccounts(feedId, None, 0, 3, sessionId))
          assert(result1(0).id == accountId5)
          assert(result1(1).id == accountId4)
          assert(result1(2).id == accountId3)

          // should return next page
          val result2 = await(feedLikesRepository.findAccounts(feedId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == accountId2)
          assert(result2(1).id == accountId1)

          // should not return when account blocked
          val result3 = await(feedLikesRepository.findAccounts(feedId, None, 0, 3, accountId4.toSessionId))
          assert(result3(0).id == accountId4)
          assert(result3(1).id == accountId3)
          assert(result3(2).id == accountId2)

          // should not return when account is blocked
          val result4 = await(feedLikesRepository.findAccounts(feedId, None, 0, 3, accountId5.toSessionId))
          assert(result4(0).id == accountId5)
          assert(result4(1).id == accountId3)
          assert(result4(2).id == accountId2)

      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(accountGen) { (s) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.findAccounts(FeedId(0), None, 0, 3, sessionId))
        }.error == FeedNotFound)
      }
    }

  }


}

