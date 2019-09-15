package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Tweets

class TweetsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create a tweet and increase tweet count") {
      forAll(userGen, tweetGen, medium5SeqOptGen) { (a, f, l) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsDAO.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        val result1 = await(db.run(quote(query[Tweets].filter(_.id == lift(tweetId))))).head
        assert(result1.message == f.message)
        assert(result1.expiration == f.expiration)
        assert(result1.privacyType == f.privacyType)
        assert(result1.contentWarning == f.contentWarning)
        assert(result1.mediumId1 == ids.map(i => i(0)))
        assert(result1.mediumId2 == ids.map(i => i(1)))
        assert(result1.mediumId3 == ids.map(i => i(2)))
        assert(result1.mediumId4 == ids.map(i => i(3)))
        assert(result1.mediumId5 == ids.map(i => i(4)))
        assert(result1.tags == f.tags)

        val result2 = await(usersDAO.find(sessionId))
        assert(result2.exists(_.tweetCount == 1))
      }
    }
  }

  feature("update") {
    scenario("should update a tweet") {
      forAll(userGen, tweetGen, tweetGen, medium5SeqOptGen, medium5SeqOptGen) { (a, f, f2, l, l2) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val tweetId = await(tweetsDAO.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))


        val ids2 = l2.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags2 = f2.tags.map(_.split(' ').toSeq)
        await(tweetsDAO.update(tweetId, f2.message, ids2, tags2, f2.privacyType, f2.contentWarning, f2.expiration, sessionId))

        val result1 = await(db.run(quote(query[Tweets].filter(_.id == lift(tweetId))))).head
        assert(result1.message == f2.message)
        assert(result1.expiration == f2.expiration)
        assert(result1.privacyType == f2.privacyType)
        assert(result1.contentWarning == f2.contentWarning)
        assert(result1.mediumId1 == ids2.map(i => i(0)))
        assert(result1.mediumId2 == ids2.map(i => i(1)))
        assert(result1.mediumId3 == ids2.map(i => i(2)))
        assert(result1.mediumId4 == ids2.map(i => i(3)))
        assert(result1.mediumId5 == ids2.map(i => i(4)))
        assert(result1.tags == f2.tags)

      }
    }
  }

  feature("delete") {
    scenario("should delete a tweet and decrease tweet count") {
      forAll(userGen, tweetGen) { (a, f) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(usersDAO.find(sessionId))
        assert(result1.exists(_.tweetCount == 1))
        await(tweetsDAO.delete(tweetId, sessionId))
        val result2 = await(usersDAO.find(sessionId))
        assert(result2.exists(_.tweetCount == 0))
      }
    }
  }

  feature("own") {
    scenario("should return own or not") {
      forOne(userGen, userGen, expiredTweetsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(tweetsDAO.own(tweetId, sessionId))
        assert(result1)
        val result2 = await(tweetsDAO.own(tweetId, userId1.sessionId))
        assert(!result2)
      }
    }
  }

  feature("findOwner") {
    scenario("should return owner or not") {
      forOne(userGen, userGen, expiredTweetsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        await(usersDAO.create(a.userName))
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(tweetsDAO.findOwner(tweetId))
        assert(result1.exists(_ == sessionId.userId))
      }
    }
  }

  feature("exists") {

    scenario("should not return when user is blocked") {
      forOne(userGen, userGen, everyoneTweetGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId1.sessionId)))
        ids.map(i => await(mediumsDAO.find(i, userId1.sessionId))).flatten

        // user1 blocked session user
        await(blocksDAO.create(sessionId.userId, userId1.sessionId))
        val tweetId = await(tweetsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId1.sessionId))

        val result = await(tweetsDAO.exists(tweetId, sessionId))
        assert(!result)
      }
    }


    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, selfTweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a friend and a followers
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return false
        val result1 = await(tweetsDAO.exists(tweetId, userId1.sessionId))
        assert(!result1)

        // friends return false
        val result2 = await(tweetsDAO.exists(tweetId, userId2.sessionId))
        assert(!result2)

        // user3 return false
        val result3 = await(tweetsDAO.exists(tweetId, userId3.sessionId))
        assert(!result3)

      }
    }
    scenario("should not return tweets if privacy type is followers and an user is not followers") {
      forOne(userGen, userGen, userGen, userGen, followerTweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a friend and a followers
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return true
        val result1 = await(tweetsDAO.exists(tweetId, userId1.sessionId))
        assert(result1)

        // friends return true
        val result2 = await(tweetsDAO.exists(tweetId, userId2.sessionId))
        assert(result2)

        // user3 return false
        val result3 = await(tweetsDAO.exists(tweetId, userId3.sessionId))
        assert(!result3)

      }
    }

    scenario("should not return tweets if privacy type is friends and an user is not friend") {
      forOne(userGen, userGen, userGen, userGen, friendTweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a friend and a followers
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return false
        val result1 = await(tweetsDAO.exists(tweetId, userId1.sessionId))
        assert(!result1)

        // friends return true
        val result2 = await(tweetsDAO.exists(tweetId, userId2.sessionId))
        assert(result2)

        // user3 return false
        val result3 = await(tweetsDAO.exists(tweetId, userId3.sessionId))
        assert(!result3)


      }
    }

    scenario("should not return expired tweets") {
      forOne(userGen, expiredTweetsGen) { (s, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val result = await(tweetsDAO.exists(tweetId, sessionId))
        assert(!result)
      }
    }




  }

  feature("find tweets") {

    scenario("should return medium1-5") {
      forOne(userGen, userGen, everyoneTweetGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId.sessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, userId.sessionId))).flatten
        val tweetId = await(tweetsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId.sessionId))
        val result = await(tweetsDAO.find(userId, None, 0, 5, sessionId))

        assert(result.size == 1)
        assert(result.headOption.exists(_.id == tweetId))
        assert(result.headOption.exists(_.likeCount == 0L))
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

    scenario("should return next page") {
      forOne(userGen, userGen, tweet20SeqGen) { (s, a1, f) =>

        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, None, sessionId))
          f.copy(id = tweetId)
        }).reverse

        // page1 found
        val result1 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 0)
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(tweetsDAO.find(sessionId.userId, result1.lastOption.map(_.next), 0, 10, userId1.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i + size1).id)
          assert(r.likeCount == 0)
        }

        // page3 not found
        val result3 = await(tweetsDAO.find(sessionId.userId, result2.lastOption.map(_.next), 0, 10, userId1.sessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        f.foreach({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.self, f.contentWarning, None, sessionId))
          f.copy(id = tweetId)
        })


        // follower
        val result1 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
        assert(result2.size == 0)

        // not follower and not friend
        val result3 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return tweets if privacy type is followers and an user is not a follower and a friend") {
      forOne(userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.followers, f.contentWarning, None, sessionId))
          f.copy(id = tweetId)
        }).reverse


        // follower
        val result1 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 0)
        }

        // friend
        val result2 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 0)
        }

        // not follower and not friend
        val result3 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return tweets when privacy type is friends and an user is not a friend") {
      forOne(userGen, userGen, userGen, userGen, tweet20SeqGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a follower and a friend
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val createdTweets = f.map({ f =>
          val tweetId = await(tweetsDAO.create(f.message, None, None, TweetPrivacyType.friends, f.contentWarning, None, sessionId))
          f.copy(id = tweetId)
        }).reverse


        // follower
        val result1 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdTweets(i).id)
          assert(r.likeCount == 0)
        }

        // not follower and not friend
        val result3 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId3.sessionId))
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

          // comment a tweet
          await(commentsDAO.create(tweetId, c, None, sessionId))
          await(commentsDAO.create(tweetId, c, None, userId1.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId2.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId3.sessionId))

          // block user
          await(blocksDAO.create(userId2, userId1.sessionId))
          await(blocksDAO.create(userId1, userId3.sessionId))

          // find by user1 and should return like count is 2
          val result1 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
          assert(result1(0).id == tweetId)
          assert(result1(0).likeCount == 2)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by user2 and should return like count is 2
          val result2 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
          assert(result2(0).id == tweetId)
          assert(result2(0).likeCount == 2)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by user2 and should return like count is 3
          val result3 = await(tweetsDAO.find(sessionId.userId, None, 0, 10, userId4.sessionId))
          assert(result3(0).id == tweetId)
          assert(result3(0).likeCount == 3)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should not return expired tweets") {
      forOne(userGen, userGen, expiredTweetsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result = await(tweetsDAO.find(userId1, None, 0, 5, sessionId))
        assert(result.size == 0)
      }
    }

  }

  feature("find a tweet") {

    scenario("should return medium1-5") {
      forOne(userGen, userGen, everyoneTweetGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId.sessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, userId.sessionId))).flatten
        val tweetId = await(tweetsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId.sessionId))
        val result = await(tweetsDAO.find(tweetId, sessionId))

        assert(result.isDefined)
        assert(result.headOption.exists(_.id == tweetId))
        assert(result.headOption.exists(_.likeCount == 0L))
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

    scenario("should not return when user blocked") {
      forOne(userGen, userGen, everyoneTweetGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        ids.map(i => await(mediumsDAO.find(i, sessionId))).flatten

        // session user blocked user1
        await(blocksDAO.create(userId1, sessionId))
        val tweetId = await(tweetsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        val result = await(tweetsDAO.find(tweetId, userId1.sessionId))
        assert(result.isEmpty)
      }
    }

    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, selfTweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a friend and a followers
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers not return a tweet
        val result1 = await(tweetsDAO.find(tweetId, userId1.sessionId))
        assert(result1.isEmpty)

        // friends not return  a tweet
        val result2 = await(tweetsDAO.find(tweetId, userId2.sessionId))
        assert(result2.isEmpty)

        // user3 not return  a tweet
        val result3 = await(tweetsDAO.find(tweetId, userId3.sessionId))
        assert(result3.isEmpty)

      }
    }
    scenario("should not return tweets if privacy type is followers and an user is not a follower and a friend") {
      forOne(userGen, userGen, userGen, userGen, followerTweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a friend and a followers
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return a tweet
        val result1 = await(tweetsDAO.find(tweetId, userId1.sessionId))
        assert(result1.isDefined)

        // friends return  a tweet
        val result2 = await(tweetsDAO.find(tweetId, userId2.sessionId))
        assert(result2.isDefined)

        // user3 not return  a tweet
        val result3 = await(tweetsDAO.find(tweetId, userId3.sessionId))
        assert(result3.isEmpty)

      }
    }

    scenario("should not return tweets when privacy type is friends and an user is not a friend") {
      forOne(userGen, userGen, userGen, userGen, friendTweetGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  user1 is a follower.
        //  user2 is a friend.
        //  user3 is not a friend and a followers
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))
        val userId2 = await(usersDAO.create(a2.userName))
        val userId3 = await(usersDAO.create(a3.userName))
        await(followsDAO.create(sessionId.userId, userId1.sessionId))
        await(followersDAO.create(sessionId.userId, userId1.sessionId))
        await(friendsDAO.create(userId2, sessionId))
        await(friendsDAO.create(sessionId.userId, userId2.sessionId))

        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers not return a tweet
        val result1 = await(tweetsDAO.find(tweetId, userId1.sessionId))
        assert(result1.isEmpty)

        // friends return a tweet
        val result2 = await(tweetsDAO.find(tweetId, userId2.sessionId))
        assert(result2.isDefined)

        // user3 not return  a tweet
        val result3 = await(tweetsDAO.find(tweetId, userId3.sessionId))
        assert(result3.isEmpty)

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

          // comment a tweet
          await(commentsDAO.create(tweetId, c, None, sessionId))
          await(commentsDAO.create(tweetId, c, None, userId1.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId2.sessionId))
          await(commentsDAO.create(tweetId, c, None, userId3.sessionId))

          // block user
          await(blocksDAO.create(userId2, userId1.sessionId))
          await(blocksDAO.create(userId1, userId3.sessionId))

          // find by user1 and should return like count is 2
          val result1 = await(tweetsDAO.find(tweetId, userId1.sessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == tweetId))
          assert(result1.exists(_.likeCount == 2))
          assert(result1.exists(_.commentCount == 3))
          assert(result1.exists(_.liked))


          // find by user2 and should return like count is 2
          val result2 = await(tweetsDAO.find(tweetId, userId2.sessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.id == tweetId))
          assert(result2.exists(_.likeCount == 2))
          assert(result2.exists(_.commentCount == 3))
          assert(result2.exists(_.liked))

          // find by user2 and should return like count is 3
          val result3 = await(tweetsDAO.find(tweetId, userId4.sessionId))
          assert(result3.isDefined)
          assert(result3.exists(_.id == tweetId))
          assert(result3.exists(_.likeCount == 3))
          assert(result3.exists(_.commentCount == 4))
          assert(result3.exists(!_.liked))

      }
    }

    scenario("should not return expired tweets") {
      forOne(userGen, expiredTweetsGen) { (s, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val tweetId = await(tweetsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result = await(tweetsDAO.find(tweetId, sessionId))
        assert(result.isEmpty)
      }
    }

  }

}

