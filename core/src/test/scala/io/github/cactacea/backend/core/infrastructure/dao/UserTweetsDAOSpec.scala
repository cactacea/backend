package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class UserTweetsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should fan out to followers and friends") {
      forAll(userGen, userGen, userGen, userGen, tweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is not follower and friend.
        //  user2 is follower.
        //  user3 is friend.
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followersDAO.create(userId2, sessionId))
        await(followsDAO.create(sessionId.userId, userId2.sessionId))
        await(friendsDAO.create(userId3, sessionId))
        await(friendsDAO.create(sessionId.userId, userId3.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(userTweetsDAO.create(tweetId, sessionId))

        f.privacyType match {
          case TweetPrivacyType.everyone => {

            // user1 is not follower and friend so return false
            assert(!existsUserTweets(tweetId, userId1))

            // user2 is a follower so return true
            assert(existsUserTweets(tweetId, userId2))

            // user3 is a friend so return true
            assert(existsUserTweets(tweetId, userId3))

          }
          case TweetPrivacyType.followers => {

            // user1 is not follower and friend so return false
            assert(!existsUserTweets(tweetId, userId1))

            // user2 is a follower so return true
            assert(existsUserTweets(tweetId, userId2))

            // user3 is a friend so return true
            assert(existsUserTweets(tweetId, userId3))


          }
          case TweetPrivacyType.friends => {

            // user1 is not follower and friend so return false
            assert(!existsUserTweets(tweetId, userId1))

            // user2 is a follower, but return false
            assert(!existsUserTweets(tweetId, userId2))

            // user3 is a friend so return true
            assert(existsUserTweets(tweetId, userId3))

          }
          case TweetPrivacyType.self => {

            // user1 is not follower and friend so return false
            assert(!existsUserTweets(tweetId, userId1))

            // user2 is a follower, but return false
            assert(!existsUserTweets(tweetId, userId2))

            // user3 is a friend, but return false
            assert(!existsUserTweets(tweetId, userId3))

          }
        }

      }

    }

    scenario("should not fan out if muted") {
      forOne(userGen, userGen, userGen, userGen, everyoneTweetGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  user1 is not follower and friend.
        //  user2 is follower, but muted session user.
        //  user3 is friend, but muted session user.
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followersDAO.create(userId2, sessionId))
        await(followsDAO.create(sessionId.userId, userId2.sessionId))
        await(friendsDAO.create(userId3, sessionId))
        await(friendsDAO.create(sessionId.userId, userId3.sessionId))
        await(mutesDAO.create(sessionId.userId, userId2.sessionId))
        await(mutesDAO.create(sessionId.userId, userId3.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(userTweetsDAO.create(tweetId, sessionId))

        // user1 is not follower and friend so return false
        assert(!existsUserTweets(tweetId, userId1))

        // user2 is a follower but muted so return false
        assert(!existsUserTweets(tweetId, userId2))

        // user3 is a friend but muted  so return false
        assert(!existsUserTweets(tweetId, userId3))
      }
    }

    scenario("should return an exception occurs when duplication") {
      forOne(userGen, userGen, userGen, userGen, everyoneTweetGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  user1 is not follower and friend.
        //  user2 is follower.
        //  user3 is friend.
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followersDAO.create(userId2, sessionId))
        await(followsDAO.create(sessionId.userId, userId2.sessionId))
        await(friendsDAO.create(userId3, sessionId))
        await(friendsDAO.create(sessionId.userId, userId3.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // exception occurs
        await(userTweetsDAO.create(tweetId, sessionId))
        assert(intercept[ServerError] {
          await(userTweetsDAO.create(tweetId, sessionId))
        }.code == 1062)

      }
    }
  }


  feature("delete") {
    scenario("should delete user tweets") {
      forOne(userGen, userGen, userGen, userGen, everyoneTweetGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  user1 is not follower and friend.
        //  user2 is follower
        //  user3 is friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followersDAO.create(userId2, sessionId))
        await(followsDAO.create(sessionId.userId, userId2.sessionId))
        await(friendsDAO.create(userId3, sessionId))
        await(friendsDAO.create(sessionId.userId, userId3.sessionId))
        await(mutesDAO.create(sessionId.userId, userId2.sessionId))
        await(mutesDAO.create(sessionId.userId, userId3.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(userTweetsDAO.create(tweetId, sessionId))

        await(userTweetsDAO.delete(tweetId))

        // user1 is not follower and friend so return false
        assert(!existsUserTweets(tweetId, userId1))

        // user2 is a follower but deleted so return false
        assert(!existsUserTweets(tweetId, userId2))

        // user3 is a friend but deleted  so return false
        assert(!existsUserTweets(tweetId, userId3))
      }
    }
  }

  feature("find") {


    scenario("should return tweets") {

      forAll(userGen, userGen, tweet20SeqGen, tweetPrivacyTypeOptGen) { (s, a1, f, t) =>

        // preparing
        //  user1 is friend.
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        await(friendsDAO.create(userId1, sessionId))
        await(friendsDAO.create(sessionId.userId, userId1.sessionId))

        // create tweets and filtering by t
        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
          await(userTweetsDAO.create(tweetId, sessionId))
          f.copy(id = tweetId)
        }).filter(_.privacyType != TweetPrivacyType.self)
          .filter(f => t.fold(true)(_ == f.privacyType)).reverse

        // page1 maybe found
        val result1 = await(userTweetsDAO.find(None, 0, 10, t, userId1.sessionId))
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 0)
        }

        // page2 maybe found
        val size1 = result1.size
        val result2 = await(userTweetsDAO.find(result1.lastOption.map(_.next), 0, 10, t, userId1.sessionId))
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i + size1).id)
          assert(r.likeCount == 0)
        }

        // page3 not found
        val result3 = await(userTweetsDAO.find(result2.lastOption.map(_.next), 0, 10, t, userId1.sessionId))
        result3.size == 0

      }
    }

    scenario("should not return if blocked or being blocked") {
      forOne(userGen, userGen, userGen, everyoneTweetGen) { (s, a1, a2, f) =>
        // preparing
        //  user1 is friend, but block session user after receive tweets.
        //  user1 is friend, but being blocked by session user after receive tweets.
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        await(friendsDAO.create(userId1, sessionId))
        await(friendsDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(userTweetsDAO.create(tweetId, sessionId))

        // user1 block session user
        await(blocksDAO.create(sessionId.userId, userId1.sessionId))

        // session user block user2
        await(blocksDAO.create(userId2, sessionId))

        // tweets found
        val result1 = await(userTweetsDAO.find(None, 0, 10, None, userId1.sessionId))
        assert(result1.size == 1)

        // tweets not found
        val result2 = await(userTweetsDAO.find(None, 0, 10, None, userId2.sessionId))
        assert(result2.size == 0)
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
          await(userTweetsDAO.create(tweetId, sessionId))

          // like a tweet
          await(tweetLikesDAO.create(tweetId, userId1.sessionId))
          await(tweetLikesDAO.create(tweetId, userId2.sessionId))
          await(tweetLikesDAO.create(tweetId, userId3.sessionId))

          // comment a tweet
          await(commentsDAO.create(tweetId, c, None, sessionId))
          await(commentsDAO.create(tweetId, c, None, userId1.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId2.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId3.sessionId))

          // block user
          await(blocksDAO.create(userId2, userId1.sessionId))
          await(blocksDAO.create(userId1, userId3.sessionId))

          // find by user1 and should return like count is 2
          val result1 = await(userTweetsDAO.find(None, 0, 10, None, userId1.sessionId))
          assert(result1(0).id == tweetId)
          assert(result1(0).likeCount == 2)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by user2 and should return like count is 2
          val result2 = await(userTweetsDAO.find(None, 0, 10, None, userId2.sessionId))
          assert(result2(0).id == tweetId)
          assert(result2(0).likeCount == 2)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by user2 and should return like count is 3
          val result3 = await(userTweetsDAO.find(None, 0, 10, None, userId4.sessionId))
          assert(result3(0).id == tweetId)
          assert(result3(0).likeCount == 3)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should return mediums") {
      forOne(userGen, userGen, everyoneTweetGen, medium5SeqGen) {
        (s, a1, f, m) =>
          // preparing
          //  user1 is friend
          val sessionId = await(usersDAO.create(s.userName)).sessionId
          val userId1 = await(usersDAO.create(a1.userName))
          await(friendsDAO.create(userId1, sessionId))
          await(friendsDAO.create(sessionId.userId, userId1.sessionId))
          val mediumsId = m.map({ m =>
            await(mediumsDAO.create(
              m.key,
              m.uri,
              m.thumbnailUrl,
              m.mediumType,
              m.width,
              m.height,
              m.size,
              sessionId))
          })
          val tweetId = await(tweetsDAO.create(f.message, Option(mediumsId), None, f.privacyType, f.contentWarning, None, sessionId))
          await(userTweetsDAO.create(tweetId, sessionId))

          // return tweet with mediums
          val result1 = await(userTweetsDAO.find(None, 0, 10, None, userId1.sessionId))
          assert(result1.size == 1)
          mediumsId.zipWithIndex.foreach({ case (id, i) =>
            assert(result1(0).mediums(i).id == id)
          })
          m.zipWithIndex.foreach({ case (m, i) =>
            assert(result1(0).mediums(i).width == m.width)
            assert(result1(0).mediums(i).height == m.height)
          })
      }
    }

    scenario("should not return expired tweets") {

      forAll(userGen, userGen, tweet20SeqGen, passDateTimeMillisGen) { (s, a1, f, t) =>

        // preparing
        //  user1 is friend.
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        await(friendsDAO.create(userId1, sessionId))
        await(friendsDAO.create(sessionId.userId, userId1.sessionId))

        // create tweets and filtering by t
        f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, Option(t), sessionId))
          await(userTweetsDAO.create(tweetId, sessionId))
          f.copy(id = tweetId)
        }).filter(_.privacyType != TweetPrivacyType.self).reverse

        val result = await(userTweetsDAO.find(None, 0, 10,  None, userId1.sessionId))
        result.size == 0

      }
    }


  }


}
