package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec


class TweetLikesDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create a tweet like and increase like count") {
      forOne(userGen, userGen, userGen, userGen, tweetGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  session user creates a tweet
        //  user1 like a tweet
        //  user2 like a tweet
        //  user3 like a tweet
        //  user1 block user2
        //  user2 block user3
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(blocksDAO.create(userId2, userId1.sessionId))
        await(blocksDAO.create(userId1, userId2.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(tweetLikesDAO.create(tweetId, userId1.sessionId))
        await(tweetLikesDAO.create(tweetId, userId2.sessionId))
        await(tweetLikesDAO.create(tweetId, userId3.sessionId))

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

    scenario("should return an exception occurs when duplication") {
      forOne(userGen, userGen, tweetGen) { (s, a1, f) =>
        // preparing
        //  session user creates a tweet
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        await(tweetLikesDAO.create(tweetId, userId1.sessionId))
        assert(intercept[ServerError] {
          await(tweetLikesDAO.create(tweetId, userId1.sessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should delete a tweet like and decrease like count") {
      forOne(userGen, userGen, userGen, userGen, tweetGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session user creates a tweet
          //  user1 like a tweet
          //  user2 like a tweet
          //  user3 like a tweet
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val userId3 = await(usersDAO.create(a3.userName))
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(tweetLikesDAO.create(tweetId, userId1.sessionId))
          await(tweetLikesDAO.create(tweetId, userId2.sessionId))
          await(tweetLikesDAO.create(tweetId, userId3.sessionId))
          await(tweetLikesDAO.delete(tweetId, userId1.sessionId))
          await(tweetLikesDAO.delete(tweetId, userId2.sessionId))
          await(tweetLikesDAO.delete(tweetId, userId3.sessionId))

          val result1 = await(tweetsDAO.find(tweetId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(tweetsDAO.find(tweetId, userId1.sessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(tweetsDAO.find(tweetId, userId2.sessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

    scenario("should delete all comment likes if tweet deleted") {
      forOne(userGen, userGen, userGen, userGen, tweetGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session user creates a tweet
          //  user1 like a tweet
          //  user2 like a tweet
          //  user3 like a tweet
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val userId3 = await(usersDAO.create(a3.userName))
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(tweetLikesDAO.create(tweetId, userId1.sessionId))
          await(tweetLikesDAO.create(tweetId, userId2.sessionId))
          await(tweetLikesDAO.create(tweetId, userId3.sessionId))
          await(tweetsDAO.delete(tweetId, sessionId))

          val result1 = await(tweetsDAO.find(tweetId, sessionId))
          assert(result1.isEmpty)
      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(userGen, userGen, userGen, userGen, tweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  session user creates a tweet
        //  user1 like a tweet
        //  user2 like a tweet
        //  user3 like a tweet
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(tweetLikesDAO.create(tweetId, userId1.sessionId))
        await(tweetLikesDAO.create(tweetId, userId2.sessionId))
        await(tweetLikesDAO.create(tweetId, userId3.sessionId))

        assert(await(tweetLikesDAO.own(tweetId, userId1.sessionId)))
        assert(await(tweetLikesDAO.own(tweetId, userId2.sessionId)))
        assert(await(tweetLikesDAO.own(tweetId, userId3.sessionId)))

        await(tweetLikesDAO.delete(tweetId, userId1.sessionId))

        assert(!await(tweetLikesDAO.own(tweetId, userId1.sessionId)))
        assert(await(tweetLikesDAO.own(tweetId, userId2.sessionId)))
        assert(await(tweetLikesDAO.own(tweetId, userId3.sessionId)))

        await(tweetLikesDAO.delete(tweetId, userId2.sessionId))

        assert(!await(tweetLikesDAO.own(tweetId, userId1.sessionId)))
        assert(!await(tweetLikesDAO.own(tweetId, userId2.sessionId)))
        assert(await(tweetLikesDAO.own(tweetId, userId3.sessionId)))

        await(tweetLikesDAO.delete(tweetId, userId3.sessionId))

        assert(!await(tweetLikesDAO.own(tweetId, userId1.sessionId)))
        assert(!await(tweetLikesDAO.own(tweetId, userId2.sessionId)))
        assert(!await(tweetLikesDAO.own(tweetId, userId3.sessionId)))
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
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val userId3 = await(usersDAO.create(a3.userName))
          val userId4 = await(usersDAO.create(a4.userName))
          val userId5 = await(usersDAO.create(a5.userName))
          await(blocksDAO.create(userId4, userId5.sessionId))
          await(blocksDAO.create(userId5, userId4.sessionId))

          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(tweetLikesDAO.create(tweetId, userId1.sessionId))
          await(tweetLikesDAO.create(tweetId, userId2.sessionId))
          await(tweetLikesDAO.create(tweetId, userId3.sessionId))
          await(tweetLikesDAO.create(tweetId, userId4.sessionId))
          await(tweetLikesDAO.create(tweetId, userId5.sessionId))

          // should return user list
          val result1 = await(tweetLikesDAO.findUsers(tweetId, None, 0, 3, sessionId))
          assert(result1(0).id == userId5)
          assert(result1(1).id == userId4)
          assert(result1(2).id == userId3)

          // should return next page
          val result2 = await(tweetLikesDAO.findUsers(tweetId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == userId2)
          assert(result2(1).id == userId1)

          // should not return when user blocked
          val result3 = await(tweetLikesDAO.findUsers(tweetId, None, 0, 3, userId4.sessionId))
          assert(result3(0).id == userId4)
          assert(result3(1).id == userId3)
          assert(result3(2).id == userId2)

          // should not return when user is blocked
          val result4 = await(tweetLikesDAO.findUsers(tweetId, None, 0, 3, userId5.sessionId))
          assert(result4(0).id == userId5)
          assert(result4(1).id == userId3)
          assert(result4(2).id == userId2)

      }
    }

  }

  feature("find") {

    scenario("should return medium1-5") {
      forOne(userGen, userGen, userGen, everyoneTweetGen, medium5SeqGen) { (s, a, a2, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId.sessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, userId.sessionId))).flatten
        val tweetId = await(tweetsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId.sessionId))
        await(tweetLikesDAO.create(tweetId, userId2.sessionId))
        val result = await(tweetLikesDAO.find(userId2, None, 0, 5, sessionId))


        assert(result.size == 1)
        assert(result.headOption.exists(_.id == tweetId))
        assert(result.headOption.exists(_.likeCount == 1L))
        assert(result.headOption.exists(_.commentCount == 0L))
        assert(result.headOption.exists(_.message == f.message))
        assert(result.headOption.exists(_.warning == f.contentWarning))
        assert(result.headOption.exists(!_.rejected))
        assert(result.headOption.exists(!_.liked))
        for (elem <- (0 to 4)) {
          assert(result.headOption.exists(_.mediums(elem).id == mediums(elem).id))
          assert(result.headOption.exists(_.mediums(elem).uri == mediums(elem).uri))
          assert(result.headOption.exists(_.mediums(elem).thumbnailUrl == mediums(elem).thumbnailUrl))
          assert(result.headOption.exists(_.mediums(elem).mediumType == mediums(elem).mediumType))
          assert(result.headOption.exists(_.mediums(elem).height == mediums(elem).height))
          assert(result.headOption.exists(_.mediums(elem).width == mediums(elem).width))
          assert(result.headOption.exists(_.mediums(elem).size == mediums(elem).size))
          assert(result.headOption.exists(_.mediums(elem).warning == mediums(elem).contentWarning))
          assert(result.headOption.exists(_.mediums(elem).mediumType == mediums(elem).mediumType))
        }
      }
    }

    scenario("should not return when user is blocked") {
      forOne(userGen, userGen, userGen, everyoneTweetGen, medium5SeqGen) { (s, a1, a2, f, l) =>
        // preparing
        //   session create a tweet
        //   user1 like tweet
        //   session block user2
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        ids.map(i => await(mediumsDAO.find(i, userId1.sessionId))).flatten
        val tweetId = await(tweetsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetLikesDAO.create(tweetId, userId1.sessionId))
        await(blocksDAO.create(userId2, sessionId))
        val result = await(tweetLikesDAO.find(userId1, None, 0, 5, userId2.sessionId))
        assert(result.size == 0)
      }
    }

    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        f.foreach({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.self, f.contentWarning, None, sessionId))
          await(tweetLikesDAO.create(tweetId, userId4.sessionId))
          f.copy(id = tweetId)
        })


        // follower
        val result1 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId2.sessionId))
        assert(result2.size == 0)

        // not follower and not friend
        val result3 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return tweets if privacy type is followers and an user is not a follower and a friend") {
      forOne(userGen, userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.followers, f.contentWarning, None, sessionId))
          await(tweetLikesDAO.create(tweetId, userId4.sessionId))
          f.copy(id = tweetId)
        }).reverse


        // follower
        val result1 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 1)
        }

        // friend
        val result2 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should not return tweets when privacy type is friends and an user is not a friend") {
      forOne(userGen, userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        val userId4 = await(usersDAO.create(a4.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.friends, f.contentWarning, None, sessionId))
          await(tweetLikesDAO.create(tweetId, userId4.sessionId))
          f.copy(id = tweetId)
        }).reverse


        // follower
        val result1 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(tweetLikesDAO.find(userId4, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should return like count, comment count and liked or not") {
      forOne(userGen,userGen,userGen,userGen,userGen,everyoneTweetGen,commentMessageGen) {
        (s, a1, a2, a3, a4, f, c) =>

          // preparing
          //  user1 is friend.
          //  user2 is friend.
          //  user4 is friend.
          //  user1 liked a tweet
          //  user2 liked a tweet
          //  user3 liked a tweet
          //  user1 blocked user2
          //  user3 blocked user1
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          val userId2 = await(usersDAO.create(a2.userName))
          val userId3 = await(usersDAO.create(a3.userName))
          val userId4 = await(usersDAO.create(a4.userName))

          // user1 is friend
          await(friendsDAO.create(userId1, sessionId))
          await(friendsDAO.create(sessionId.userId, userId1.sessionId))

          // user2 is friend
          await(friendsDAO.create(userId2, sessionId))
          await(friendsDAO.create(sessionId.userId, userId2.sessionId))

          // user4 is friend
          await(friendsDAO.create(userId4, sessionId))
          await(friendsDAO.create(sessionId.userId, userId4.sessionId))

          // create and fan out a tweet
          val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

          // like a tweet
          await(tweetLikesDAO.create(tweetId, userId1.sessionId))
          await(tweetLikesDAO.create(tweetId, userId2.sessionId))
          await(tweetLikesDAO.create(tweetId, userId3.sessionId))

          await(tweetLikesDAO.create(tweetId, sessionId))

          // comment a tweet
          await(commentsDAO.create(tweetId, c, None, sessionId))
          await(commentsDAO.create(tweetId, c, None, userId1.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId2.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId3.sessionId))

          // block user
          await(blocksDAO.create(userId2, userId1.sessionId))
          await(blocksDAO.create(userId1, userId3.sessionId))

          // find by user1 and should return like count is 3
          val result1 = await(tweetLikesDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
          assert(result1(0).id == tweetId)
          assert(result1(0).likeCount == 3)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by user2 and should return like count is 3
          val result2 = await(tweetLikesDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
          assert(result2(0).id == tweetId)
          assert(result2(0).likeCount == 3)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by user2 and should return like count is 3
          val result3 = await(tweetLikesDAO.find(sessionId.userId, None, 0, 10, userId4.sessionId))
          assert(result3(0).id == tweetId)
          assert(result3(0).likeCount == 4)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should not return expired tweets") {
      forOne(userGen, userGen, expiredTweetsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(tweetLikesDAO.create(tweetId, userId1.sessionId))
        val result = await(tweetLikesDAO.find(userId1, None, 0, 5, sessionId))
        assert(result.size == 0)
      }
    }

  }

}

