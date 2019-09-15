package io.github.cactacea.backend.core.domain.repositories

import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{CommentAlreadyLiked, CommentNotFound, CommentNotLiked}

class CommentLikesRepositorySpec extends RepositorySpec {


  feature("create") {
    scenario("should create a comment liked") {

      forOne(userGen, userGen, userGen, userGen, tweetGen, commentGen) { (s, a1, a2, a3, f, c) =>
        // preparing
        //  session user creates a tweet
        //  session user create a comment
        //  user1 like a comment
        //  user2 like a comment
        //  user3 like a comment
        //  user1 block user2
        //  user2 block user3
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val userId3 = await(createUser(a3.userName)).id
        await(blocksRepository.create(userId2, userId1.sessionId))
        await(blocksRepository.create(userId1, userId2.sessionId))
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
        await(commentLikesRepository.create(commentId, userId1.sessionId))
        await(commentLikesRepository.create(commentId, userId2.sessionId))
        await(commentLikesRepository.create(commentId, userId3.sessionId))

        // should like count 3
        val result1 = await(commentsRepository.find(commentId, sessionId))
        assert(result1.likeCount == 3)

        // should like count 2 because user1 blocked user2
        val result2 = await(commentsRepository.find(commentId, userId1.sessionId))
        assert(result2.likeCount == 2)

        // should like count 2 because user2 blocked user1
        val result3 = await(commentsRepository.find(commentId, userId2.sessionId))
        assert(result3.likeCount == 2)
      }

    }

    scenario("should return exception if a comment already liked") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>
        // preparing
        //  session user creates a tweet
        //  session user create a comment
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))

        // exception occurs
        await(commentLikesRepository.create(commentId, userId1.sessionId))
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.create(commentId, userId1.sessionId))
        }.error == CommentAlreadyLiked)
      }
    }

    scenario("should return exception if a comment not exist") {
      forOne(userGen) { (s) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.create(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }

  }


  feature("delete") {

    scenario("should delete a comment liked") {

      forOne(userGen, userGen, userGen, userGen, tweetGen, commentGen) {
        (s, a1, a2, a3, f, c) =>
          // preparing
          //  session user creates a tweet
          //  session user create a comment
          //  user1 like a comment
          //  user2 like a comment
          //  user3 like a comment
          val sessionId = await(createUser(s.userName)).id.sessionId
          val userId1 = await(createUser(a1.userName)).id
          val userId2 = await(createUser(a2.userName)).id
          val userId3 = await(createUser(a3.userName)).id
          val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
          await(commentLikesRepository.create(commentId, userId1.sessionId))
          await(commentLikesRepository.create(commentId, userId2.sessionId))
          await(commentLikesRepository.create(commentId, userId3.sessionId))

          await(commentLikesRepository.delete(commentId, userId1.sessionId))
          await(commentLikesRepository.delete(commentId, userId2.sessionId))
          await(commentLikesRepository.delete(commentId, userId3.sessionId))

          assertFutureValue(commentLikesDAO.own(commentId, sessionId), false)

      }
    }

    scenario("should return exception if a comment not liked") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>
        // preparing
        //  session user creates a tweet
        //  session user create a comment
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.delete(commentId, userId1.sessionId))
        }.error == CommentNotLiked)
      }
    }

    scenario("should return exception if a comment not exist") {
      forOne(userGen) { (s) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.delete(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }


  }


  feature("findUsers") {

    scenario("should return user list who liked a comment") {
      forOne(userGen, userGen, userGen, userGen, userGen, userGen, tweetGen, commentGen) {
        (s, a1, a2, a3, a4, a5, f, c) =>

          // preparing
          //  session user creates a tweet
          //  session user create a comment
          //  user1 like a comment
          //  user2 like a comment
          //  user3 like a comment
          //  user4 like a comment
          //  user5 like a comment
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

          val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
          await(commentLikesRepository.create(commentId, userId1.sessionId))
          await(commentLikesRepository.create(commentId, userId2.sessionId))
          await(commentLikesRepository.create(commentId, userId3.sessionId))
          await(commentLikesRepository.create(commentId, userId4.sessionId))
          await(commentLikesRepository.create(commentId, userId5.sessionId))

          // should return user list
          val result1 = await(commentLikesRepository.findUsers(commentId, None, 0, 3, sessionId))
          assert(result1(0).id == userId5)
          assert(result1(1).id == userId4)
          assert(result1(2).id == userId3)

          // should return next page
          val result2 = await(commentLikesRepository.findUsers(commentId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == userId2)
          assert(result2(1).id == userId1)

          // should not return when user blocked
          val result3 = await(commentLikesRepository.findUsers(commentId, None, 0, 3, userId4.sessionId))
          assert(result3(0).id == userId4)
          assert(result3(1).id == userId3)
          assert(result3(2).id == userId2)

          // should not return when user is blocked
          val result4 = await(commentLikesRepository.findUsers(commentId, None, 0, 3, userId5.sessionId))
          assert(result4(0).id == userId5)
          assert(result4(1).id == userId3)
          assert(result4(2).id == userId2)

      }
    }

    scenario("should return exception if a comment not exist") {
      forOne(userGen) { (s) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.findUsers(CommentId(0), None, 0, 3, sessionId))
        }.error == CommentNotFound)
      }
    }

  }

}
