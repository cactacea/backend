package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Feeds

class FeedsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create a feed and increase feed count") {
      forAll(userGen, feedGen, medium5SeqOptGen) { (a, f, l) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val feedId = await(feedsDAO.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))

        val result1 = await(db.run(quote(query[Feeds].filter(_.id == lift(feedId))))).head
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
        assert(result2.exists(_.feedCount == 1))
      }
    }
  }

  feature("update") {
    scenario("should update a feed") {
      forAll(userGen, feedGen, feedGen, medium5SeqOptGen, medium5SeqOptGen) { (a, f, f2, l, l2) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toSeq)
        val feedId = await(feedsDAO.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))


        val ids2 = l2.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags2 = f2.tags.map(_.split(' ').toSeq)
        await(feedsDAO.update(feedId, f2.message, ids2, tags2, f2.privacyType, f2.contentWarning, f2.expiration, sessionId))

        val result1 = await(db.run(quote(query[Feeds].filter(_.id == lift(feedId))))).head
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
    scenario("should delete a feed and decrease feed count") {
      forAll(userGen, feedGen) { (a, f) =>
        val sessionId = await(usersDAO.create(a.userName)).sessionId
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(usersDAO.find(sessionId))
        assert(result1.exists(_.feedCount == 1))
        await(feedsDAO.delete(feedId, sessionId))
        val result2 = await(usersDAO.find(sessionId))
        assert(result2.exists(_.feedCount == 0))
      }
    }
  }

  feature("own") {
    scenario("should return own or not") {
      forOne(userGen, userGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(feedsDAO.own(feedId, sessionId))
        assert(result1)
        val result2 = await(feedsDAO.own(feedId, userId1.sessionId))
        assert(!result2)
      }
    }
  }

  feature("findOwner") {
    scenario("should return owner or not") {
      forOne(userGen, userGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        await(usersDAO.create(a.userName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(feedsDAO.findOwner(feedId))
        assert(result1.exists(_ == sessionId.userId))
      }
    }
  }

  feature("exists") {

    scenario("should not return when user is blocked") {
      forOne(userGen, userGen, everyoneFeedGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId1.sessionId)))
        ids.map(i => await(mediumsDAO.find(i, userId1.sessionId))).flatten

        // user1 blocked session user
        await(blocksDAO.create(sessionId.userId, userId1.sessionId))
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId1.sessionId))

        val result = await(feedsDAO.exists(feedId, sessionId))
        assert(!result)
      }
    }


    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, selfFeedGen) { (s, a1, a2, a3, f) =>

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

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return false
        val result1 = await(feedsDAO.exists(feedId, userId1.sessionId))
        assert(!result1)

        // friends return false
        val result2 = await(feedsDAO.exists(feedId, userId2.sessionId))
        assert(!result2)

        // user3 return false
        val result3 = await(feedsDAO.exists(feedId, userId3.sessionId))
        assert(!result3)

      }
    }
    scenario("should not return feeds if privacy type is followers and an user is not followers") {
      forOne(userGen, userGen, userGen, userGen, followerFeedGen) { (s, a1, a2, a3, f) =>

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

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return true
        val result1 = await(feedsDAO.exists(feedId, userId1.sessionId))
        assert(result1)

        // friends return true
        val result2 = await(feedsDAO.exists(feedId, userId2.sessionId))
        assert(result2)

        // user3 return false
        val result3 = await(feedsDAO.exists(feedId, userId3.sessionId))
        assert(!result3)

      }
    }

    scenario("should not return feeds if privacy type is friends and an user is not friend") {
      forOne(userGen, userGen, userGen, userGen, friendFeedGen) { (s, a1, a2, a3, f) =>

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

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return false
        val result1 = await(feedsDAO.exists(feedId, userId1.sessionId))
        assert(!result1)

        // friends return true
        val result2 = await(feedsDAO.exists(feedId, userId2.sessionId))
        assert(result2)

        // user3 return false
        val result3 = await(feedsDAO.exists(feedId, userId3.sessionId))
        assert(!result3)


      }
    }

    scenario("should not return expired feeds") {
      forOne(userGen, expiredFeedsGen) { (s, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val result = await(feedsDAO.exists(feedId, sessionId))
        assert(!result)
      }
    }




  }

  feature("find feeds") {

    scenario("should return medium1-5") {
      forOne(userGen, userGen, everyoneFeedGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId.sessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, userId.sessionId))).flatten
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId.sessionId))
        val result = await(feedsDAO.find(userId, None, 0, 5, sessionId))

        assert(result.size == 1)
        assert(result.headOption.exists(_.id == feedId))
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
      forOne(userGen, userGen, feed20SeqGen) { (s, a1, f) =>

        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a1.userName))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        }).reverse

        // page1 found
        val result1 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(feedsDAO.find(sessionId.userId, result1.lastOption.map(_.next), 0, 10, userId1.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i + size1).id)
          assert(r.likeCount == 0)
        }

        // page3 not found
        val result3 = await(feedsDAO.find(sessionId.userId, result2.lastOption.map(_.next), 0, 10, userId1.sessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, feed20SeqGen) { (s, a1, a2, a3, f) =>

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
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.self, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        })


        // follower
        val result1 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
        assert(result2.size == 0)

        // not follower and not friend
        val result3 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return feeds if privacy type is followers and an user is not a follower and a friend") {
      forOne(userGen, userGen, userGen, userGen, feed20SeqGen) { (s, a1, a2, a3, f) =>

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

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.followers, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // friend
        val result2 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // not follower and not friend
        val result3 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return feeds when privacy type is friends and an user is not a friend") {
      forOne(userGen, userGen, userGen, userGen, feed20SeqGen) { (s, a1, a2, a3, f) =>

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

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.friends, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // not follower and not friend
        val result3 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId3.sessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should return like count, comment count and liked or not") {
      forOne(userGen,userGen,userGen,userGen,userGen,everyoneFeedGen,commentMessageGen) {
        (s, a1, a2, a3, a4, f, c) =>

          // preparing
          //  user1 is friend.
          //  user2 is friend.
          //  user4 is friend.
          //  user1 liked a feed
          //  user2 liked a feed
          //  user3 liked a feed
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

          // create and fan out a feed
          val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

          // like a feed
          await(feedLikesDAO.create(feedId, userId1.sessionId))
          await(feedLikesDAO.create(feedId, userId2.sessionId))
          await(feedLikesDAO.create(feedId, userId3.sessionId))

          // comment a feed
          await(commentsDAO.create(feedId, c, None, sessionId))
          await(commentsDAO.create(feedId, c, None, userId1.sessionId))
          await(commentsDAO.create(feedId, c, None, userId2.sessionId))
          await(commentsDAO.create(feedId, c, None, userId3.sessionId))

          // block user
          await(blocksDAO.create(userId2, userId1.sessionId))
          await(blocksDAO.create(userId1, userId3.sessionId))

          // find by user1 and should return like count is 2
          val result1 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId1.sessionId))
          assert(result1(0).id == feedId)
          assert(result1(0).likeCount == 2)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by user2 and should return like count is 2
          val result2 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId2.sessionId))
          assert(result2(0).id == feedId)
          assert(result2(0).likeCount == 2)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by user2 and should return like count is 3
          val result3 = await(feedsDAO.find(sessionId.userId, None, 0, 10, userId4.sessionId))
          assert(result3(0).id == feedId)
          assert(result3(0).likeCount == 3)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should not return expired feeds") {
      forOne(userGen, userGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result = await(feedsDAO.find(userId1, None, 0, 5, sessionId))
        assert(result.size == 0)
      }
    }

  }

  feature("find a feed") {

    scenario("should return medium1-5") {
      forOne(userGen, userGen, everyoneFeedGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, userId.sessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, userId.sessionId))).flatten
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, userId.sessionId))
        val result = await(feedsDAO.find(feedId, sessionId))

        assert(result.isDefined)
        assert(result.headOption.exists(_.id == feedId))
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
      forOne(userGen, userGen, everyoneFeedGen, medium5SeqGen) { (s, a, f, l) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val userId1 = await(usersDAO.create(a.userName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        ids.map(i => await(mediumsDAO.find(i, sessionId))).flatten

        // session user blocked user1
        await(blocksDAO.create(userId1, sessionId))
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        val result = await(feedsDAO.find(feedId, userId1.sessionId))
        assert(result.isEmpty)
      }
    }

    scenario("should not return privacy type is self") {
      forOne(userGen, userGen, userGen, userGen, selfFeedGen) { (s, a1, a2, a3, f) =>

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

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers not return a feed
        val result1 = await(feedsDAO.find(feedId, userId1.sessionId))
        assert(result1.isEmpty)

        // friends not return  a feed
        val result2 = await(feedsDAO.find(feedId, userId2.sessionId))
        assert(result2.isEmpty)

        // user3 not return  a feed
        val result3 = await(feedsDAO.find(feedId, userId3.sessionId))
        assert(result3.isEmpty)

      }
    }
    scenario("should not return feeds if privacy type is followers and an user is not a follower and a friend") {
      forOne(userGen, userGen, userGen, userGen, followerFeedGen) { (s, a1, a2, a3, f) =>

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

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return a feed
        val result1 = await(feedsDAO.find(feedId, userId1.sessionId))
        assert(result1.isDefined)

        // friends return  a feed
        val result2 = await(feedsDAO.find(feedId, userId2.sessionId))
        assert(result2.isDefined)

        // user3 not return  a feed
        val result3 = await(feedsDAO.find(feedId, userId3.sessionId))
        assert(result3.isEmpty)

      }
    }

    scenario("should not return feeds when privacy type is friends and an user is not a friend") {
      forOne(userGen, userGen, userGen, userGen, friendFeedGen) { (s, a1, a2, a3, f) =>

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

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers not return a feed
        val result1 = await(feedsDAO.find(feedId, userId1.sessionId))
        assert(result1.isEmpty)

        // friends return a feed
        val result2 = await(feedsDAO.find(feedId, userId2.sessionId))
        assert(result2.isDefined)

        // user3 not return  a feed
        val result3 = await(feedsDAO.find(feedId, userId3.sessionId))
        assert(result3.isEmpty)

      }
    }

    scenario("should return like count, comment count and liked or not") {
      forOne(userGen,userGen,userGen,userGen,userGen,everyoneFeedGen,commentMessageGen) {
        (s, a1, a2, a3, a4, f, c) =>

          // preparing
          //  user1 is friend.
          //  user2 is friend.
          //  user4 is friend.
          //  user1 liked a feed
          //  user2 liked a feed
          //  user3 liked a feed
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

          // create and fan out a feed
          val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

          // like a feed
          await(feedLikesDAO.create(feedId, userId1.sessionId))
          await(feedLikesDAO.create(feedId, userId2.sessionId))
          await(feedLikesDAO.create(feedId, userId3.sessionId))

          // comment a feed
          await(commentsDAO.create(feedId, c, None, sessionId))
          await(commentsDAO.create(feedId, c, None, userId1.sessionId))
          await(commentsDAO.create(feedId, c, None, userId2.sessionId))
          await(commentsDAO.create(feedId, c, None, userId3.sessionId))

          // block user
          await(blocksDAO.create(userId2, userId1.sessionId))
          await(blocksDAO.create(userId1, userId3.sessionId))

          // find by user1 and should return like count is 2
          val result1 = await(feedsDAO.find(feedId, userId1.sessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == feedId))
          assert(result1.exists(_.likeCount == 2))
          assert(result1.exists(_.commentCount == 3))
          assert(result1.exists(_.liked))


          // find by user2 and should return like count is 2
          val result2 = await(feedsDAO.find(feedId, userId2.sessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.id == feedId))
          assert(result2.exists(_.likeCount == 2))
          assert(result2.exists(_.commentCount == 3))
          assert(result2.exists(_.liked))

          // find by user2 and should return like count is 3
          val result3 = await(feedsDAO.find(feedId, userId4.sessionId))
          assert(result3.isDefined)
          assert(result3.exists(_.id == feedId))
          assert(result3.exists(_.likeCount == 3))
          assert(result3.exists(_.commentCount == 4))
          assert(result3.exists(!_.liked))

      }
    }

    scenario("should not return expired feeds") {
      forOne(userGen, expiredFeedsGen) { (s, f) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result = await(feedsDAO.find(feedId, sessionId))
        assert(result.isEmpty)
      }
    }

  }

}

