package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FeedId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class FeedLikesRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a feed like") {
      forOne(userGen, userGen, userGen, userGen, feedGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  session user create a feed
        //  user1 like a feed
        //  user2 like a feed
        //  user3 like a feed
        //  user1 block user2
        //  user2 block user3
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(a1.userName)).id
        val userId2 = await(usersRepository.create(a2.userName)).id
        val userId3 = await(usersRepository.create(a3.userName)).id
        await(blocksRepository.create(userId2, userId1.sessionId))
        await(blocksRepository.create(userId1, userId2.sessionId))
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(feedLikesRepository.create(feedId, userId1.sessionId))
        await(feedLikesRepository.create(feedId, userId2.sessionId))
        await(feedLikesRepository.create(feedId, userId3.sessionId))

        // should like count 3
        val result1 = await(feedsDAO.find(feedId, sessionId))
        assert(result1.map(_.likeCount) == Option(3))

        // should like count 2 because user1 blocked user2
        val result2 = await(feedsDAO.find(feedId, userId1.sessionId))
        assert(result2.map(_.likeCount) == Option(2))

        // should like count 2 because user2 blocked user1
        val result3 = await(feedsDAO.find(feedId, userId2.sessionId))
        assert(result3.map(_.likeCount) == Option(2))
      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(userGen) { (a1) =>

        // preparing
        val userId1 = await(usersRepository.create(a1.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.create(FeedId(0), userId1.sessionId))
        }.error == FeedNotFound)
      }
    }

    scenario("should return exception if a feed already liked") {
      forOne(userGen, userGen, feedGen) { (s, a1, f) =>
        // preparing
        //  session user create a feed
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(a1.userName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        await(feedLikesRepository.create(feedId, userId1.sessionId))
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.create(feedId, userId1.sessionId))
        }.error == FeedAlreadyLiked)
      }
    }
  }

  feature("delete") {

    scenario("should delete a feed like") {
      forOne(userGen, userGen, userGen, userGen, feedGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session user creates a feed
          //  user1 like a feed
          //  user2 like a feed
          //  user3 like a feed
          val sessionId = await(usersRepository.create(s.userName)).id.sessionId
          val userId1 = await(usersRepository.create(a1.userName)).id
          val userId2 = await(usersRepository.create(a2.userName)).id
          val userId3 = await(usersRepository.create(a3.userName)).id
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesRepository.create(feedId, userId1.sessionId))
          await(feedLikesRepository.create(feedId, userId2.sessionId))
          await(feedLikesRepository.create(feedId, userId3.sessionId))
          await(feedLikesRepository.delete(feedId, userId1.sessionId))
          await(feedLikesRepository.delete(feedId, userId2.sessionId))
          await(feedLikesRepository.delete(feedId, userId3.sessionId))

          val result1 = await(feedsDAO.find(feedId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(feedsDAO.find(feedId, userId1.sessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(feedsDAO.find(feedId, userId2.sessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(userGen) { (a1) =>

        // preparing
        val userId1 = await(usersRepository.create(a1.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.delete(FeedId(0), userId1.sessionId))
        }.error == FeedNotFound)
      }
    }

    scenario("should return exception if a feed not liked") {
      forOne(userGen, userGen, feedGen) { (s, a1, f) =>
        // preparing
        //  session user create a feed
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(a1.userName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.delete(feedId, userId1.sessionId))
        }.error == FeedNotLiked)
      }
    }

  }

  feature("find - feeds an user liked") {

    scenario("should return feed list an user liked") {
      forOne(userGen, userGen, userGen, userGen, userGen, feed20ListGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        //  user4 is a follower.
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(a1.userName)).id
        val userId2 = await(usersRepository.create(a2.userName)).id
        val userId3 = await(usersRepository.create(a3.userName)).id
        val userId4 = await(usersRepository.create(a4.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        await(followsRepository.create(sessionId.userId, userId4.sessionId))
        val requestId = await(friendRequestsRepository.create(userId2, sessionId))
        await(friendRequestsRepository.accept(requestId, userId2.sessionId))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.followers, f.contentWarning, None, sessionId))
          await(feedLikesRepository.create(feedId, userId4.sessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedLikesRepository.find(userId4, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // friend
        val result2 = await(feedLikesRepository.find(userId4, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(feedLikesRepository.find(userId4, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)

      }
    }
    
    scenario("should return exception if an user not exist") {
      forOne(userGen) { (a1) =>

        // preparing
        val userId1 = await(usersRepository.create(a1.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.find(UserId(0), None, 0, 10, userId1.sessionId))
        }.error == UserNotFound)
      }
    }

  }


  feature("find - feeds session user liked") {
    scenario("should return feed list session user liked") {

      forOne(userGen, userGen, feed20ListGen) { (s, a1, f) =>

        // preparing
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId
        val userId1 = await(usersRepository.create(a1.userName)).id

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, None, sessionId))
          await(feedLikesRepository.create(feedId, userId1.sessionId))
          f.copy(id = feedId)
        }).reverse


        val result1 = await(feedLikesRepository.find(None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

      }


    }
  }


  feature("findUsers") {
    scenario("should return user list who liked a feed") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen, feedGen) {
        (s, a1, a2, a3, a4, a5, f) =>

          // preparing
          //  session user creates a feed
          //  user1 like a feed
          //  user2 like a feed
          //  user3 like a feed
          //  user4 like a feed
          //  user5 like a feed
          //  user4 block user5
          //  user5 block user4
          val sessionId = await(usersRepository.create(s.userName)).id.sessionId
          val userId1 = await(usersRepository.create(a1.userName)).id
          val userId2 = await(usersRepository.create(a2.userName)).id
          val userId3 = await(usersRepository.create(a3.userName)).id
          val userId4 = await(usersRepository.create(a4.userName)).id
          val userId5 = await(usersRepository.create(a5.userName)).id
          await(blocksRepository.create(userId4, userId5.sessionId))
          await(blocksRepository.create(userId5, userId4.sessionId))

          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesRepository.create(feedId, userId1.sessionId))
          await(feedLikesRepository.create(feedId, userId2.sessionId))
          await(feedLikesRepository.create(feedId, userId3.sessionId))
          await(feedLikesRepository.create(feedId, userId4.sessionId))
          await(feedLikesRepository.create(feedId, userId5.sessionId))

          // should return user list
          val result1 = await(feedLikesRepository.findUsers(feedId, None, 0, 3, sessionId))
          assert(result1(0).id == userId5)
          assert(result1(1).id == userId4)
          assert(result1(2).id == userId3)

          // should return next page
          val result2 = await(feedLikesRepository.findUsers(feedId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == userId2)
          assert(result2(1).id == userId1)

          // should not return when user blocked
          val result3 = await(feedLikesRepository.findUsers(feedId, None, 0, 3, userId4.sessionId))
          assert(result3(0).id == userId4)
          assert(result3(1).id == userId3)
          assert(result3(2).id == userId2)

          // should not return when user is blocked
          val result4 = await(feedLikesRepository.findUsers(feedId, None, 0, 3, userId5.sessionId))
          assert(result4(0).id == userId5)
          assert(result4(1).id == userId3)
          assert(result4(2).id == userId2)

      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(userGen) { (s) =>

        // preparing
        val sessionId = await(usersRepository.create(s.userName)).id.sessionId

        // exception occurs
        assert(intercept[CactaceaException] {
          await(feedLikesRepository.findUsers(FeedId(0), None, 0, 3, sessionId))
        }.error == FeedNotFound)
      }
    }

  }


}

