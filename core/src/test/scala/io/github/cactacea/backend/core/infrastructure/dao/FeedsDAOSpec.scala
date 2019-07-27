package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.models.Feeds

class FeedsDAOSpec extends DAOSpec {

  import db._

  feature("create") {
    scenario("should create a feed and increase feed count") {
      forAll(accountGen, feedGen, medium5ListOptGen) { (a, f, l) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toList)
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

        val result2 = await(accountsDAO.find(sessionId))
        assert(result2.exists(_.feedCount == 1))
      }
    }
  }

  feature("update") {
    scenario("should update a feed") {
      forAll(accountGen, feedGen, feedGen, medium5ListOptGen, medium5ListOptGen) { (a, f, f2, l, l2) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val ids = l.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags = f.tags.map(_.split(' ').toList)
        val feedId = await(feedsDAO.create(f.message, ids, tags, f.privacyType, f.contentWarning, f.expiration, sessionId))


        val ids2 = l2.map(_.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId))))
        val tags2 = f2.tags.map(_.split(' ').toList)
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
      forAll(accountGen, feedGen) { (a, f) =>
        val sessionId = await(accountsDAO.create(a.accountName)).toSessionId
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(accountsDAO.find(sessionId))
        assert(result1.exists(_.feedCount == 1))
        await(feedsDAO.delete(feedId, sessionId))
        val result2 = await(accountsDAO.find(sessionId))
        assert(result2.exists(_.feedCount == 0))
      }
    }
  }

  feature("own") {
    scenario("should return own or not") {
      forOne(accountGen, accountGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(feedsDAO.own(feedId, sessionId))
        assert(result1)
        val result2 = await(feedsDAO.own(feedId, accountId1.toSessionId))
        assert(!result2)
      }
    }
  }

  feature("findOwner") {
    scenario("should return owner or not") {
      forOne(accountGen, accountGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(accountsDAO.create(a.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result1 = await(feedsDAO.findOwner(feedId))
        assert(result1.exists(_ == sessionId.toAccountId))
      }
    }
  }

  feature("exists") {

    scenario("should not return when account is blocked") {
      forOne(accountGen, accountGen, everyoneFeedGen, medium5ListGen) { (s, a, f, l) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a.accountName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, accountId1.toSessionId)))
        ids.map(i => await(mediumsDAO.find(i, accountId1.toSessionId))).flatten

        // account1 blocked session account
        await(blocksDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, accountId1.toSessionId))

        val result = await(feedsDAO.exists(feedId, sessionId))
        assert(!result)
      }
    }


    scenario("should not return privacy type is self") {
      forOne(accountGen, accountGen, accountGen, accountGen, selfFeedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a friend and a followers
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return false
        val result1 = await(feedsDAO.exists(feedId, accountId1.toSessionId))
        assert(!result1)

        // friends return false
        val result2 = await(feedsDAO.exists(feedId, accountId2.toSessionId))
        assert(!result2)

        // account3 return false
        val result3 = await(feedsDAO.exists(feedId, accountId3.toSessionId))
        assert(!result3)

      }
    }
    scenario("should not return feeds if privacy type is followers and an account is not followers") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerFeedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a friend and a followers
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return true
        val result1 = await(feedsDAO.exists(feedId, accountId1.toSessionId))
        assert(result1)

        // friends return true
        val result2 = await(feedsDAO.exists(feedId, accountId2.toSessionId))
        assert(result2)

        // account3 return false
        val result3 = await(feedsDAO.exists(feedId, accountId3.toSessionId))
        assert(!result3)

      }
    }

    scenario("should not return feeds if privacy type is friends and an account is not friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendFeedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a friend and a followers
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return false
        val result1 = await(feedsDAO.exists(feedId, accountId1.toSessionId))
        assert(!result1)

        // friends return true
        val result2 = await(feedsDAO.exists(feedId, accountId2.toSessionId))
        assert(result2)

        // account3 return false
        val result3 = await(feedsDAO.exists(feedId, accountId3.toSessionId))
        assert(!result3)


      }
    }

    scenario("should not return expired feeds") {
      forOne(accountGen, expiredFeedsGen) { (s, f) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val result = await(feedsDAO.exists(feedId, sessionId))
        assert(!result)
      }
    }




  }

  feature("find feeds") {

    scenario("should return medium1-5") {
      forOne(accountGen, accountGen, everyoneFeedGen, medium5ListGen) { (s, a, f, l) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, accountId.toSessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, accountId.toSessionId))).flatten
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, accountId.toSessionId))
        val result = await(feedsDAO.find(accountId, None, 0, 5, sessionId))

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
      forOne(accountGen, accountGen, feed20ListGen) { (s, a1, f) =>

        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        }).reverse

        // page1 found
        val result1 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // page2 found
        val size1 = result1.size
        val result2 = await(feedsDAO.find(sessionId.toAccountId, result1.lastOption.map(_.next), 0, 10, accountId1.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i + size1).id)
          assert(r.likeCount == 0)
        }

        // page3 not found
        val result3 = await(feedsDAO.find(sessionId.toAccountId, result2.lastOption.map(_.next), 0, 10, accountId1.toSessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should not return privacy type is self") {
      forOne(accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        f.foreach({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.self, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        })


        // follower
        val result1 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 0)

        // not follower and not friend
        val result3 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId3.toSessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return feeds if privacy type is followers and an account is not a follower and a friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.followers, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // friend
        val result2 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // not follower and not friend
        val result3 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId3.toSessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return feeds when privacy type is friends and an account is not a friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.friends, f.contentWarning, None, sessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // not follower and not friend
        val result3 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId3.toSessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should return like count, comment count and liked or not") {
      forOne(accountGen,accountGen,accountGen,accountGen,accountGen,everyoneFeedGen,commentMessageGen) {
        (s, a1, a2, a3, a4, f, c) =>

          // preparing
          //  account1 is friend.
          //  account2 is friend.
          //  account4 is friend.
          //  account1 liked a feed
          //  account2 liked a feed
          //  account3 liked a feed
          //  account1 blocked account2
          //  account3 blocked account1
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val accountId3 = await(accountsDAO.create(a3.accountName))
          val accountId4 = await(accountsDAO.create(a4.accountName))

          // account1 is friend
          await(friendsDAO.create(accountId1, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId1.toSessionId))

          // account2 is friend
          await(friendsDAO.create(accountId2, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

          // account4 is friend
          await(friendsDAO.create(accountId4, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId4.toSessionId))

          // create and fan out a feed
          val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

          // like a feed
          await(feedLikesDAO.create(feedId, accountId1.toSessionId))
          await(feedLikesDAO.create(feedId, accountId2.toSessionId))
          await(feedLikesDAO.create(feedId, accountId3.toSessionId))

          // comment a feed
          await(commentsDAO.create(feedId, c, None, sessionId))
          await(commentsDAO.create(feedId, c, None, accountId1.toSessionId))
          await(commentsDAO.create(feedId, c, None, accountId2.toSessionId))
          await(commentsDAO.create(feedId, c, None, accountId3.toSessionId))

          // block account
          await(blocksDAO.create(accountId2, accountId1.toSessionId))
          await(blocksDAO.create(accountId1, accountId3.toSessionId))

          // find by account1 and should return like count is 2
          val result1 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId1.toSessionId))
          assert(result1(0).id == feedId)
          assert(result1(0).likeCount == 2)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by account2 and should return like count is 2
          val result2 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId2.toSessionId))
          assert(result2(0).id == feedId)
          assert(result2(0).likeCount == 2)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by account2 and should return like count is 3
          val result3 = await(feedsDAO.find(sessionId.toAccountId, None, 0, 10, accountId4.toSessionId))
          assert(result3(0).id == feedId)
          assert(result3(0).likeCount == 3)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should not return expired feeds") {
      forOne(accountGen, accountGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a.accountName))
        await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result = await(feedsDAO.find(accountId1, None, 0, 5, sessionId))
        assert(result.size == 0)
      }
    }

  }

  feature("find a feed") {

    scenario("should return medium1-5") {
      forOne(accountGen, accountGen, everyoneFeedGen, medium5ListGen) { (s, a, f, l) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, accountId.toSessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, accountId.toSessionId))).flatten
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, accountId.toSessionId))
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

    scenario("should not return when account blocked") {
      forOne(accountGen, accountGen, everyoneFeedGen, medium5ListGen) { (s, a, f, l) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a.accountName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        ids.map(i => await(mediumsDAO.find(i, sessionId))).flatten

        // session account blocked account1
        await(blocksDAO.create(accountId1, sessionId))
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, sessionId))

        val result = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(result.isEmpty)
      }
    }

    scenario("should not return privacy type is self") {
      forOne(accountGen, accountGen, accountGen, accountGen, selfFeedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a friend and a followers
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers not return a feed
        val result1 = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(result1.isEmpty)

        // friends not return  a feed
        val result2 = await(feedsDAO.find(feedId, accountId2.toSessionId))
        assert(result2.isEmpty)

        // account3 not return  a feed
        val result3 = await(feedsDAO.find(feedId, accountId3.toSessionId))
        assert(result3.isEmpty)

      }
    }
    scenario("should not return feeds if privacy type is followers and an account is not a follower and a friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, followerFeedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a friend and a followers
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers return a feed
        val result1 = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(result1.isDefined)

        // friends return  a feed
        val result2 = await(feedsDAO.find(feedId, accountId2.toSessionId))
        assert(result2.isDefined)

        // account3 not return  a feed
        val result3 = await(feedsDAO.find(feedId, accountId3.toSessionId))
        assert(result3.isEmpty)

      }
    }

    scenario("should not return feeds when privacy type is friends and an account is not a friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, friendFeedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a friend and a followers
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // followers not return a feed
        val result1 = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(result1.isEmpty)

        // friends return a feed
        val result2 = await(feedsDAO.find(feedId, accountId2.toSessionId))
        assert(result2.isDefined)

        // account3 not return  a feed
        val result3 = await(feedsDAO.find(feedId, accountId3.toSessionId))
        assert(result3.isEmpty)

      }
    }

    scenario("should return like count, comment count and liked or not") {
      forOne(accountGen,accountGen,accountGen,accountGen,accountGen,everyoneFeedGen,commentMessageGen) {
        (s, a1, a2, a3, a4, f, c) =>

          // preparing
          //  account1 is friend.
          //  account2 is friend.
          //  account4 is friend.
          //  account1 liked a feed
          //  account2 liked a feed
          //  account3 liked a feed
          //  account1 blocked account2
          //  account3 blocked account1
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val accountId3 = await(accountsDAO.create(a3.accountName))
          val accountId4 = await(accountsDAO.create(a4.accountName))

          // account1 is friend
          await(friendsDAO.create(accountId1, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId1.toSessionId))

          // account2 is friend
          await(friendsDAO.create(accountId2, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

          // account4 is friend
          await(friendsDAO.create(accountId4, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId4.toSessionId))

          // create and fan out a feed
          val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

          // like a feed
          await(feedLikesDAO.create(feedId, accountId1.toSessionId))
          await(feedLikesDAO.create(feedId, accountId2.toSessionId))
          await(feedLikesDAO.create(feedId, accountId3.toSessionId))

          // comment a feed
          await(commentsDAO.create(feedId, c, None, sessionId))
          await(commentsDAO.create(feedId, c, None, accountId1.toSessionId))
          await(commentsDAO.create(feedId, c, None, accountId2.toSessionId))
          await(commentsDAO.create(feedId, c, None, accountId3.toSessionId))

          // block account
          await(blocksDAO.create(accountId2, accountId1.toSessionId))
          await(blocksDAO.create(accountId1, accountId3.toSessionId))

          // find by account1 and should return like count is 2
          val result1 = await(feedsDAO.find(feedId, accountId1.toSessionId))
          assert(result1.isDefined)
          assert(result1.exists(_.id == feedId))
          assert(result1.exists(_.likeCount == 2))
          assert(result1.exists(_.commentCount == 3))
          assert(result1.exists(_.liked))


          // find by account2 and should return like count is 2
          val result2 = await(feedsDAO.find(feedId, accountId2.toSessionId))
          assert(result2.isDefined)
          assert(result2.exists(_.id == feedId))
          assert(result2.exists(_.likeCount == 2))
          assert(result2.exists(_.commentCount == 3))
          assert(result2.exists(_.liked))

          // find by account2 and should return like count is 3
          val result3 = await(feedsDAO.find(feedId, accountId4.toSessionId))
          assert(result3.isDefined)
          assert(result3.exists(_.id == feedId))
          assert(result3.exists(_.likeCount == 3))
          assert(result3.exists(_.commentCount == 4))
          assert(result3.exists(!_.liked))

      }
    }

    scenario("should not return expired feeds") {
      forOne(accountGen, expiredFeedsGen) { (s, f) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        val result = await(feedsDAO.find(feedId, sessionId))
        assert(result.isEmpty)
      }
    }

  }

}

