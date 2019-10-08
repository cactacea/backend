package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec


class CommentLikesDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should return like count") {

      forOne(userGen, userGen, userGen, userGen, tweetGen, commentGen) { (s, a1, a2, a3, f, c) =>
        // preparing
        //  session user creates a tweet
        //  session user create a comment
        //  user1 like a comment
        //  user2 like a comment
        //  user3 like a comment
        //  user1 block user2
        //  user2 block user3
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(blocksDAO.create(userId2, userId1.sessionId))
        await(blocksDAO.create(userId1, userId2.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))
        await(commentLikesDAO.create(commentId, userId1.sessionId))
        await(commentLikesDAO.create(commentId, userId2.sessionId))
        await(commentLikesDAO.create(commentId, userId3.sessionId))

        // should like count 3
        val result1 = await(commentsDAO.find(commentId, sessionId))
        assert(result1.map(_.likeCount) == Option(3))

        // should like count 2 because user1 blocked user2
        val result2 = await(commentsDAO.find(commentId, userId1.sessionId))
        assert(result2.map(_.likeCount) == Option(2))

        // should like count 2 because user2 blocked user1
        val result3 = await(commentsDAO.find(commentId, userId2.sessionId))
        assert(result3.map(_.likeCount) == Option(2))
      }

    }

    scenario("should return an exception occurs when duplication") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>
        // preparing
        //  session user creates a tweet
        //  session user create a comment
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))

        // exception occurs
        await(commentLikesDAO.create(commentId, userId1.sessionId))
        assert(intercept[ServerError] {
          await(commentLikesDAO.create(commentId, userId1.sessionId))
        }.code == 1062)
      }
    }

  }


  feature("delete") {
    scenario("should delete a comment like and decrease like count") {
      forOne(userGen, userGen, userGen, userGen, tweetGen, commentGen) {
        (s, a1, a2, a3, f, c) =>
          // preparing
          //  session user creates a tweet
          //  session user create a comment
          //  user1 like a comment
          //  user2 like a comment
          //  user3 like a comment
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val userId3 = await(usersDAO.create(a3.userName))
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))
          await(commentLikesDAO.create(commentId, userId1.sessionId))
          await(commentLikesDAO.create(commentId, userId2.sessionId))
          await(commentLikesDAO.create(commentId, userId3.sessionId))

          await(commentLikesDAO.delete(commentId, userId1.sessionId))
          await(commentLikesDAO.delete(commentId, userId2.sessionId))
          await(commentLikesDAO.delete(commentId, userId3.sessionId))

          val result1 = await(commentsDAO.find(commentId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(commentsDAO.find(commentId, userId1.sessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(commentsDAO.find(commentId, userId2.sessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(userGen, userGen, userGen, userGen, tweetGen, commentGen) { (s, a1, a2, a3, f, c) =>

        // preparing
        //  session user creates a tweet
        //  session user create a comment
        //  user1 like a comment
        //  user2 like a comment
        //  user3 like a comment
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))
        await(commentLikesDAO.create(commentId, userId1.sessionId))
        await(commentLikesDAO.create(commentId, userId2.sessionId))
        await(commentLikesDAO.create(commentId, userId3.sessionId))

        assert(await(commentLikesDAO.own(commentId, userId1.sessionId)))
        assert(await(commentLikesDAO.own(commentId, userId2.sessionId)))
        assert(await(commentLikesDAO.own(commentId, userId3.sessionId)))

        await(commentLikesDAO.delete(commentId, userId1.sessionId))

        assert(!await(commentLikesDAO.own(commentId, userId1.sessionId)))
        assert(await(commentLikesDAO.own(commentId, userId2.sessionId)))
        assert(await(commentLikesDAO.own(commentId, userId3.sessionId)))

        await(commentLikesDAO.delete(commentId, userId2.sessionId))

        assert(!await(commentLikesDAO.own(commentId, userId1.sessionId)))
        assert(!await(commentLikesDAO.own(commentId, userId2.sessionId)))
        assert(await(commentLikesDAO.own(commentId, userId3.sessionId)))

        await(commentLikesDAO.delete(commentId, userId3.sessionId))

        assert(!await(commentLikesDAO.own(commentId, userId1.sessionId)))
        assert(!await(commentLikesDAO.own(commentId, userId2.sessionId)))
        assert(!await(commentLikesDAO.own(commentId, userId3.sessionId)))
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
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val userId3 = await(usersDAO.create(a3.userName))
          val userId4 = await(usersDAO.create(a4.userName))
          val userId5 = await(usersDAO.create(a5.userName))
          await(blocksDAO.create(userId4, userId5.sessionId))
          await(blocksDAO.create(userId5, userId4.sessionId))

          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))
          await(commentLikesDAO.create(commentId, userId1.sessionId))
          await(commentLikesDAO.create(commentId, userId2.sessionId))
          await(commentLikesDAO.create(commentId, userId3.sessionId))
          await(commentLikesDAO.create(commentId, userId4.sessionId))
          await(commentLikesDAO.create(commentId, userId5.sessionId))

          // should return user list
          val result1 = await(commentLikesDAO.findUsers(commentId, None, 0, 3, sessionId))
          assert(result1(0).id == userId5)
          assert(result1(1).id == userId4)
          assert(result1(2).id == userId3)

          // should return next page
          val result2 = await(commentLikesDAO.findUsers(commentId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == userId2)
          assert(result2(1).id == userId1)

          // should not return when user blocked
          val result3 = await(commentLikesDAO.findUsers(commentId, None, 0, 3, userId4.sessionId))
          assert(result3(0).id == userId4)
          assert(result3(1).id == userId3)
          assert(result3(2).id == userId2)

          // should not return when user is blocked
          val result4 = await(commentLikesDAO.findUsers(commentId, None, 0, 3, userId5.sessionId))
          assert(result4(0).id == userId5)
          assert(result4(1).id == userId3)
          assert(result4(2).id == userId2)

      }
    }
  }

}

