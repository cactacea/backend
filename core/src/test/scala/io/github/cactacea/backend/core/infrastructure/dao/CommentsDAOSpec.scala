package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId

class CommentsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a comment") {
      forAll(userGen, userGen, userGen, tweetGen, comment20SeqGen) { (s, a1, a2, f, c) =>

        // preparing
        //  user1 create a tweet
        //  user2 create a comment
        //  user1 create a comment
        //  user2 create a comment
        //  user1 create a comment
        //  user2 create a comment
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, userId1.sessionId))
        val commentId1 = await(commentsDAO.create(tweetId, c(0).message, None, userId2.sessionId))
        val commentId2 = await(commentsDAO.create(tweetId, c(1).message, None, userId1.sessionId))
        val commentId3 = await(commentsDAO.create(tweetId, c(2).message, None, userId2.sessionId))
        val commentId4 = await(commentsDAO.create(tweetId, c(3).message, None, userId1.sessionId))
        val commentId5 = await(commentsDAO.create(tweetId, c(4).message, None, userId2.sessionId))

        // return comment1 exist
        // return comment2 exist
        // return comment3 exist
        // return comment4 exist
        // return comment5 exist
        assert(await(commentsDAO.own(commentId1, userId2.sessionId)))
        assert(await(commentsDAO.own(commentId2, userId1.sessionId)))
        assert(await(commentsDAO.own(commentId3, userId2.sessionId)))
        assert(await(commentsDAO.own(commentId4, userId1.sessionId)))
        assert(await(commentsDAO.own(commentId5, userId2.sessionId)))

        // return comment count 5
        val tweet = await(tweetsDAO.find(tweetId, sessionId))
        assert(tweet.map(_.commentCount) == Option(5L))
      }
    }
  }

  feature("delete") {
    scenario("should delete a comment") {
      forOne(userGen, userGen, userGen, tweetGen, comment20SeqGen) { (s, a1, a2, f, c) =>

        // preparing
        //  user1 create a tweet
        //  user2 create and delete a comment
        //  user1 create and delete a comment
        //  user2 create and delete a comment
        //  user1 create and delete a comment
        //  user2 create and delete a comment
        await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, userId1.sessionId))
        val commentId1 = await(commentsDAO.create(tweetId, c(0).message, None, userId2.sessionId))
        val commentId2 = await(commentsDAO.create(tweetId, c(1).message, None, userId1.sessionId))
        val commentId3 = await(commentsDAO.create(tweetId, c(2).message, None, userId2.sessionId))
        val commentId4 = await(commentsDAO.create(tweetId, c(3).message, None, userId1.sessionId))
        val commentId5 = await(commentsDAO.create(tweetId, c(4).message, None, userId2.sessionId))
        await(commentsDAO.delete(tweetId, commentId1, userId2.sessionId))
        await(commentsDAO.delete(tweetId, commentId2, userId1.sessionId))
        await(commentsDAO.delete(tweetId, commentId3, userId2.sessionId))
        await(commentsDAO.delete(tweetId, commentId4, userId1.sessionId))
        await(commentsDAO.delete(tweetId, commentId5, userId2.sessionId))

        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        assert(!await(commentsDAO.own(commentId1, userId2.sessionId)))
        assert(!await(commentsDAO.own(commentId2, userId1.sessionId)))
        assert(!await(commentsDAO.own(commentId3, userId2.sessionId)))
        assert(!await(commentsDAO.own(commentId4, userId1.sessionId)))
        assert(!await(commentsDAO.own(commentId5, userId2.sessionId)))

        // return comment count 0
        val tweet = await(tweetsDAO.find(tweetId, userId1.sessionId))
        assert(tweet.exists(_.commentCount == 0L))
      }
    }

    scenario("should delete all comments") {
      forOne(userGen, userGen, userGen, tweetGen, comment20SeqGen) { (s, a1, a2, f, c) =>

        // preparing
        //  user1 create a tweet
        //  user2 create and delete a comment
        //  user1 create and delete a comment
        //  user2 create and delete a comment
        //  user1 create and delete a comment
        //  user2 create and delete a comment
        await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, userId1.sessionId))
        val commentId1 = await(commentsDAO.create(tweetId, c(0).message, None, userId2.sessionId))
        val commentId2 = await(commentsDAO.create(tweetId, c(1).message, None, userId1.sessionId))
        val commentId3 = await(commentsDAO.create(tweetId, c(2).message, None, userId2.sessionId))
        val commentId4 = await(commentsDAO.create(tweetId, c(3).message, None, userId1.sessionId))
        val commentId5 = await(commentsDAO.create(tweetId, c(4).message, None, userId2.sessionId))
        await(tweetsDAO.delete(tweetId, userId1.sessionId))

        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        assert(!await(commentsDAO.own(commentId1, userId2.sessionId)))
        assert(!await(commentsDAO.own(commentId2, userId1.sessionId)))
        assert(!await(commentsDAO.own(commentId3, userId2.sessionId)))
        assert(!await(commentsDAO.own(commentId4, userId1.sessionId)))
        assert(!await(commentsDAO.own(commentId5, userId2.sessionId)))

      }
    }

  }

  feature("exists") {

    scenario("should return a comment exist or not") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>

        // preparing
        //  session user block user1
        //  session create a tweet
        //  session create a comment on the tweet
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        await(blocksDAO.create(userId1, sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))

        // return exist by session user
        // return not exist by user1 because user1 be blocked by session user
        // return no exist comment
        assert(await(commentsDAO.exists(commentId, sessionId)))
        assert(!await(commentsDAO.exists(commentId, userId1.sessionId)))
        assert(!await(commentsDAO.exists(CommentId(0L), sessionId)))

        // return no exist because a comment is deleted
        await(commentsDAO.delete(tweetId, commentId, sessionId))
        assert(!await(commentsDAO.exists(commentId, sessionId)))
      }
    }

    scenario("should return a tweet comment exist or not") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a1, f, c) =>

        // preparing
        //  session user block user1
        //  session create a tweet
        //  session create a comment on the tweet
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        await(blocksDAO.create(userId1, sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))

        // return exist by session user
        // return not exist by user1 because user1 be blocked by session user
        // return no exist comment
        assert(await(commentsDAO.exists(tweetId, commentId, sessionId)))
        assert(!await(commentsDAO.exists(tweetId, commentId, userId1.sessionId)))
        assert(!await(commentsDAO.exists(tweetId, CommentId(0L), sessionId)))

        // return no exist because a comment is deleted
        await(commentsDAO.delete(tweetId, commentId, sessionId))
        assert(!await(commentsDAO.exists(tweetId, commentId, sessionId)))
      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(userGen, userGen, tweetGen, commentGen) { (a1, a2, f, c) =>

        // preparing
        //  session user block user1
        //  session create a tweet
        //  session create a comment on the tweet
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val userId = await(usersDAO.create(a2.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))

        // return true because of owner
        // return false because of not owner
        assert(await(commentsDAO.own(commentId, sessionId)))
        assert(!await(commentsDAO.own(commentId, userId.sessionId)))

        // return false because of deleted
        await(commentsDAO.delete(tweetId, commentId, sessionId))
        assert(!await(commentsDAO.own(commentId, sessionId)))
      }
    }
  }

  feature("findOwner") {
    scenario("should return owner") {
      forOne(userGen, tweetGen, commentGen) { (a1, f, c) =>

        // preparing
        val sessionId = await(usersDAO.create(a1.userName)).sessionId
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))

        assertFutureValue(commentsDAO.findOwner(commentId), Option(sessionId.userId))
      }
    }
  }

  feature("find by commentId") {
    scenario("should return a comment") {
      forOne(userGen, userGen, userGen, userGen, tweetGen, commentGen) { (s, a1, a2, a3, f, c) =>

        // preparing
        //  session user create a tweet
        //  session user create a comment
        //  session user block user1
        //  session user2 block session user
        //  user1 like a comment
        //  user2 like a comment
        //  user3 like a comment
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(tweetId, c.message, None, sessionId))

        await(blocksDAO.create(userId1, sessionId))
        await(blocksDAO.create(sessionId.userId, userId2.sessionId))
        await(blocksDAO.create(userId3, userId1.sessionId))

        await(commentLikesDAO.create(commentId, userId1.sessionId))
        await(commentLikesDAO.create(commentId, userId2.sessionId))
        await(commentLikesDAO.create(commentId, userId3.sessionId))

        // should return a comment and like count
        val result1 = await(commentsDAO.find(commentId, sessionId))
        assert(result1.isDefined)
        assert(result1.map(_.id) == Option(commentId))
        assert(result1.map(_.message) == Option(c.message))
        assert(result1.map(_.likeCount) == Option(2L))

        // should not return when user is blocked
        val result2 = await(commentsDAO.find(commentId, userId1.sessionId))
        assert(result2.isEmpty)

        // should return when user blocked
        val result3 = await(commentsDAO.find(commentId, userId2.sessionId))
        assert(result3.isDefined)
        assert(result3.map(_.id) == Option(commentId))
        assert(result3.map(_.message) == Option(c.message))
        assert(result3.map(_.likeCount) == Option(3L))

        // should return like count but ignore blocked and be blocked user like count
        val result4 = await(commentsDAO.find(commentId, userId3.sessionId))
        assert(result4.isDefined)
        assert(result4.map(_.id) == Option(commentId))
        assert(result4.map(_.message) == Option(c.message))
        assert(result4.map(_.likeCount) == Option(2L))

      }
    }
  }

  feature("find") {

    scenario("should return comment list") {
      forOne(userGen, userGen, userGen, tweetGen, comment20SeqGen) {
        (s, a1, a2, f, c) =>

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
          await(blocksDAO.create(userId2, sessionId))
          await(blocksDAO.create(sessionId.userId, userId1.sessionId))
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val comments = c.map({c =>
            val id = await(commentsDAO.create(tweetId, c.message, None, sessionId))
            c.copy(id = id)
          }).reverse

          // should return comment list
          val result1 = await(commentsDAO.find(tweetId, None, 0, 10, sessionId))
          result1.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
          })

          // should return next page
          val size2 = result1.size
          val result2 = await(commentsDAO.find(tweetId, result1.lastOption.map(_.next), 0, 10, sessionId))
          result2.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i + size2).id)
          })

          // should not return when user blocked
          assert(await(commentsDAO.find(tweetId, None, 0, 10, userId1.sessionId)).nonEmpty)

          // should not return when user is blocked
          assert(await(commentsDAO.find(tweetId, None, 0, 10, userId2.sessionId)).isEmpty)

      }
    }


    scenario("should return like count") {
      forOne(userGen, userGen, userGen, userGen, tweetGen, comment20SeqGen) {
        (s, a1, a2, a3, f, c) =>
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
          val comments = c.map({c =>
            val id = await(commentsDAO.create(tweetId, c.message, None, sessionId))
            await(commentLikesDAO.create(id, userId1.sessionId))
            await(commentLikesDAO.create(id, userId2.sessionId))
            await(commentLikesDAO.create(id, userId3.sessionId))
            c.copy(id = id)
          }).reverse

          // should like count 3
          val result1 = await(commentsDAO.find(tweetId, None, 0, 10, sessionId))
          result1.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
            assert(c.likeCount == 3)
          })

          // should like count 2 because user1 blocked user2
          val result2 = await(commentsDAO.find(tweetId, None, 0, 10, userId1.sessionId))
          result2.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
            assert(c.likeCount == 2)
          })

          // should like count 2 because user2 blocked user1
          val result3 = await(commentsDAO.find(tweetId, None, 0, 10, userId2.sessionId))
          result3.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
            assert(c.likeCount == 2)
          })
      }
    }

  }

}

