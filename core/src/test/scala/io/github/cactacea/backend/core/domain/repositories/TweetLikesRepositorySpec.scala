package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId, UserId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

class TweetLikesRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a tweet like") {
      forOne(userGen, userGen, userGen, userGen, tweetGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  session user create a tweet
        //  user1 like a tweet
        //  user2 like a tweet
        //  user3 like a tweet
        //  user1 block user2
        //  user2 block user3
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(blocksRepository.create(userId2, userId1.sessionId))
        await(blocksRepository.create(userId1, userId2.sessionId))
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(tweetLikesRepository.create(tweetId, userId1.sessionId))
        await(tweetLikesRepository.create(tweetId, userId2.sessionId))
        await(tweetLikesRepository.create(tweetId, userId3.sessionId))

        // should like count 3
        val result1 = await(tweetsDAO.find(tweetId, sessionId))
        assert(result1.map(_.likeCount) == Option(3))

        // should like count 2 because user1 blocked user2
        val result2 = await(tweetsDAO.find(tweetId, userId1.sessionId))
        assert(result2.map(_.likeCount) == Option(2))

        // should like count 2 because user2 blocked user1
        val result3 = await(tweetsDAO.find(tweetId, userId2.sessionId))
        assert(result3.map(_.likeCount) == Option(2))
      }
    }

    scenario("should return exception if a tweet not exist") {
      forOne(userGen) { (a1) =>

        // preparing
        val userId1 = await(createUser(a1.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(tweetLikesRepository.create(TweetId(0), userId1.sessionId))
        }.error == TweetNotFound)
      }
    }

    scenario("should return exception if a tweet already liked") {
      forOne(userGen, userGen, tweetGen) { (s, a1, f) =>
        // preparing
        //  session user create a tweet
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        await(tweetLikesRepository.create(tweetId, userId1.sessionId))
        assert(intercept[CactaceaException] {
          await(tweetLikesRepository.create(tweetId, userId1.sessionId))
        }.error == TweetAlreadyLiked)
      }
    }
  }

  feature("delete") {

    scenario("should delete a tweet like") {
      forOne(userGen, userGen, userGen, userGen, tweetGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session user creates a tweet
          //  user1 like a tweet
          //  user2 like a tweet
          //  user3 like a tweet
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val userId2 = await(createUser(a2.userName)).id
          val userId3 = await(createUser(a3.userName)).id
          val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(tweetLikesRepository.create(tweetId, userId1.sessionId))
          await(tweetLikesRepository.create(tweetId, userId2.sessionId))
          await(tweetLikesRepository.create(tweetId, userId3.sessionId))
          await(tweetLikesRepository.delete(tweetId, userId1.sessionId))
          await(tweetLikesRepository.delete(tweetId, userId2.sessionId))
          await(tweetLikesRepository.delete(tweetId, userId3.sessionId))

          val result1 = await(tweetsDAO.find(tweetId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(tweetsDAO.find(tweetId, userId1.sessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(tweetsDAO.find(tweetId, userId2.sessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

    scenario("should return exception if a tweet not exist") {
      forOne(userGen) { (a1) =>

        // preparing
        val userId1 = await(createUser(a1.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(tweetLikesRepository.delete(TweetId(0), userId1.sessionId))
        }.error == TweetNotFound)
      }
    }

    scenario("should return exception if a tweet not liked") {
      forOne(userGen, userGen, tweetGen) { (s, a1, f) =>
        // preparing
        //  session user create a tweet
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(tweetLikesRepository.delete(tweetId, userId1.sessionId))
        }.error == TweetNotLiked)
      }
    }

  }

  feature("find - tweets an user liked") {

    scenario("should return tweet list an user liked") {
      forOne(userGen, userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        //  user4 is a follower.
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        val userId4 = await(createUser(a4.userName)).id
        await(followsRepository.create(sessionId.userId, userId1.sessionId))
        await(followsRepository.create(sessionId.userId, userId4.sessionId))
        val requestId = await(friendRequestsRepository.create(userId2, sessionId))
        await(friendRequestsRepository.accept(requestId, userId2.sessionId))

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.followers, f.contentWarning, None, sessionId))
          await(tweetLikesRepository.create(tweetId, userId4.sessionId))
          f.copy(id = tweetId)
        }).reverse


        // follower
        val result1 = await(tweetLikesRepository.find(userId4, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 1)
        }

        // friend
        val result2 = await(tweetLikesRepository.find(userId4, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(tweetLikesRepository.find(userId4, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)

      }
    }
    
    scenario("should return exception if an user not exist") {
      forOne(userGen) { (a1) =>

        // preparing
        val userId1 = await(createUser(a1.userName)).id

        // exception occurs
        assert(intercept[CactaceaException] {
          await(tweetLikesRepository.find(UserId(0), None, 0, 10, userId1.sessionId))
        }.error == UserNotFound)
      }
    }

  }


  feature("find - tweets session user liked") {
    scenario("should return tweet list session user liked") {

      forOne(userGen, userGen, tweet20SeqGen) { (s, a1, f) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, None, sessionId))
          await(tweetLikesRepository.create(tweetId, userId1.sessionId))
          f.copy(id = tweetId)
        }).reverse


        val result1 = await(tweetLikesRepository.find(None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 1)
        }

      }


    }
  }


  feature("findUsers") {
    scenario("should return user list who liked a tweet") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen, tweetGen) {
        (s, a1, a2, a3, a4, a5, f) =>

          // preparing
          //  session user creates a tweet
          //  user1 like a tweet
          //  user2 like a tweet
          //  user3 like a tweet
          //  user4 like a tweet
          //  user5 like a tweet
          //  user4 block user5
          //  user5 block user4
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val userId2 = await(createUser(a2.userName)).id
          val userId3 = await(createUser(a3.userName)).id
          val userId4 = await(createUser(a4.userName)).id
          val userId5 = await(createUser(a5.userName)).id
          await(blocksRepository.create(userId4, userId5.sessionId))
          await(blocksRepository.create(userId5, userId4.sessionId))

          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(tweetLikesRepository.create(tweetId, userId1.sessionId))
          await(tweetLikesRepository.create(tweetId, userId2.sessionId))
          await(tweetLikesRepository.create(tweetId, userId3.sessionId))
          await(tweetLikesRepository.create(tweetId, userId4.sessionId))
          await(tweetLikesRepository.create(tweetId, userId5.sessionId))

          // should return user list
          val result1 = await(tweetLikesRepository.findUsers(tweetId, None, 0, 3, sessionId))
          assert(result1(0).id == userId5)
          assert(result1(1).id == userId4)
          assert(result1(2).id == userId3)

          // should return next page
          val result2 = await(tweetLikesRepository.findUsers(tweetId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == userId2)
          assert(result2(1).id == userId1)

          // should not return when user blocked
          val result3 = await(tweetLikesRepository.findUsers(tweetId, None, 0, 3, userId4.sessionId))
          assert(result3(0).id == userId4)
          assert(result3(1).id == userId3)
          assert(result3(2).id == userId2)

          // should not return when user is blocked
          val result4 = await(tweetLikesRepository.findUsers(tweetId, None, 0, 3, userId5.sessionId))
          assert(result4(0).id == userId5)
          assert(result4(1).id == userId3)
          assert(result4(2).id == userId2)

      }
    }

    scenario("should return exception if a tweet not exist") {
      forOne(userGen) { (s) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId

        // exception occurs
        assert(intercept[CactaceaException] {
          await(tweetLikesRepository.findUsers(TweetId(0), None, 0, 3, sessionId))
        }.error == TweetNotFound)
      }
    }

  }


}

