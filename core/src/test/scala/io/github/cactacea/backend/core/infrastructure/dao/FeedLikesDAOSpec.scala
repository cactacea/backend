package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec


class FeedLikesDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should create a feed like and increase like count") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  session account creates a feed
        //  account1 like a feed
        //  account2 like a feed
        //  account3 like a feed
        //  account1 block account2
        //  account2 block account3
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(blocksDAO.create(accountId2, accountId1.toSessionId))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(feedLikesDAO.create(feedId, accountId1.toSessionId))
        await(feedLikesDAO.create(feedId, accountId2.toSessionId))
        await(feedLikesDAO.create(feedId, accountId3.toSessionId))

        // should like count 3
        val result1 = await(feedsDAO.find(feedId, sessionId))
        assert(result1.map(_.likeCount) == Option(3))

        // should like count 2 because account1 blocked account2
        val result2 = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(result2.map(_.likeCount) == Option(2))

        // should like count 2 because account2 blocked account1
        val result3 = await(feedsDAO.find(feedId, accountId2.toSessionId))
        assert(result3.map(_.likeCount) == Option(2))
      }
    }

    scenario("should return an exception occurs when duplication") {
      forOne(accountGen, accountGen, feedGen) { (s, a1, f) =>
        // preparing
        //  session account creates a feed
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))

        // exception occurs
        await(feedLikesDAO.create(feedId, accountId1.toSessionId))
        assert(intercept[ServerError] {
          await(feedLikesDAO.create(feedId, accountId1.toSessionId))
        }.code == 1062)
      }
    }

  }

  feature("delete") {
    scenario("should delete a feed like and decrease like count") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session account creates a feed
          //  account1 like a feed
          //  account2 like a feed
          //  account3 like a feed
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val accountId3 = await(accountsDAO.create(a3.accountName))
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesDAO.create(feedId, accountId1.toSessionId))
          await(feedLikesDAO.create(feedId, accountId2.toSessionId))
          await(feedLikesDAO.create(feedId, accountId3.toSessionId))
          await(feedLikesDAO.delete(feedId, accountId1.toSessionId))
          await(feedLikesDAO.delete(feedId, accountId2.toSessionId))
          await(feedLikesDAO.delete(feedId, accountId3.toSessionId))

          val result1 = await(feedsDAO.find(feedId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(feedsDAO.find(feedId, accountId1.toSessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(feedsDAO.find(feedId, accountId2.toSessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

    scenario("should delete all comment likes if feed deleted") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) {
        (s, a1, a2, a3, f) =>
          // preparing
          //  session account creates a feed
          //  account1 like a feed
          //  account2 like a feed
          //  account3 like a feed
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val accountId3 = await(accountsDAO.create(a3.accountName))
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesDAO.create(feedId, accountId1.toSessionId))
          await(feedLikesDAO.create(feedId, accountId2.toSessionId))
          await(feedLikesDAO.create(feedId, accountId3.toSessionId))
          await(feedsDAO.delete(feedId, sessionId))

          val result1 = await(feedsDAO.find(feedId, sessionId))
          assert(result1.isEmpty)
      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  session account creates a feed
        //  account1 like a feed
        //  account2 like a feed
        //  account3 like a feed
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        await(feedLikesDAO.create(feedId, accountId1.toSessionId))
        await(feedLikesDAO.create(feedId, accountId2.toSessionId))
        await(feedLikesDAO.create(feedId, accountId3.toSessionId))

        assert(await(feedLikesDAO.own(feedId, accountId1.toSessionId)))
        assert(await(feedLikesDAO.own(feedId, accountId2.toSessionId)))
        assert(await(feedLikesDAO.own(feedId, accountId3.toSessionId)))

        await(feedLikesDAO.delete(feedId, accountId1.toSessionId))

        assert(!await(feedLikesDAO.own(feedId, accountId1.toSessionId)))
        assert(await(feedLikesDAO.own(feedId, accountId2.toSessionId)))
        assert(await(feedLikesDAO.own(feedId, accountId3.toSessionId)))

        await(feedLikesDAO.delete(feedId, accountId2.toSessionId))

        assert(!await(feedLikesDAO.own(feedId, accountId1.toSessionId)))
        assert(!await(feedLikesDAO.own(feedId, accountId2.toSessionId)))
        assert(await(feedLikesDAO.own(feedId, accountId3.toSessionId)))

        await(feedLikesDAO.delete(feedId, accountId3.toSessionId))

        assert(!await(feedLikesDAO.own(feedId, accountId1.toSessionId)))
        assert(!await(feedLikesDAO.own(feedId, accountId2.toSessionId)))
        assert(!await(feedLikesDAO.own(feedId, accountId3.toSessionId)))
      }
    }
  }

  feature("findAccounts") {
    scenario("should return account list who liked a feed") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen, feedGen) {
        (s, a1, a2, a3, a4, a5, f) =>

          // preparing
          //  session account creates a feed
          //  account1 like a feed
          //  account2 like a feed
          //  account3 like a feed
          //  account4 like a feed
          //  account5 like a feed
          //  account4 block account5
          //  account5 block account4
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val accountId3 = await(accountsDAO.create(a3.accountName))
          val accountId4 = await(accountsDAO.create(a4.accountName))
          val accountId5 = await(accountsDAO.create(a5.accountName))
          await(blocksDAO.create(accountId4, accountId5.toSessionId))
          await(blocksDAO.create(accountId5, accountId4.toSessionId))

          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          await(feedLikesDAO.create(feedId, accountId1.toSessionId))
          await(feedLikesDAO.create(feedId, accountId2.toSessionId))
          await(feedLikesDAO.create(feedId, accountId3.toSessionId))
          await(feedLikesDAO.create(feedId, accountId4.toSessionId))
          await(feedLikesDAO.create(feedId, accountId5.toSessionId))

          // should return account list
          val result1 = await(feedLikesDAO.findAccounts(feedId, None, 0, 3, sessionId))
          assert(result1(0).id == accountId5)
          assert(result1(1).id == accountId4)
          assert(result1(2).id == accountId3)

          // should return next page
          val result2 = await(feedLikesDAO.findAccounts(feedId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == accountId2)
          assert(result2(1).id == accountId1)

          // should not return when account blocked
          val result3 = await(feedLikesDAO.findAccounts(feedId, None, 0, 3, accountId4.toSessionId))
          assert(result3(0).id == accountId4)
          assert(result3(1).id == accountId3)
          assert(result3(2).id == accountId2)

          // should not return when account is blocked
          val result4 = await(feedLikesDAO.findAccounts(feedId, None, 0, 3, accountId5.toSessionId))
          assert(result4(0).id == accountId5)
          assert(result4(1).id == accountId3)
          assert(result4(2).id == accountId2)

      }
    }

  }

  feature("find") {

    scenario("should return medium1-5") {
      forOne(accountGen, accountGen, accountGen, everyoneFeedGen, medium5ListGen) { (s, a, a2, f, l) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, accountId.toSessionId)))
        val mediums = ids.map(i => await(mediumsDAO.find(i, accountId.toSessionId))).flatten
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, accountId.toSessionId))
        await(feedLikesDAO.create(feedId, accountId2.toSessionId))
        val result = await(feedLikesDAO.find(accountId2, None, 0, 5, sessionId))


        assert(result.size == 1)
        assert(result.headOption.exists(_.id == feedId))
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

    scenario("should not return when account is blocked") {
      forOne(accountGen, accountGen, accountGen, everyoneFeedGen, medium5ListGen) { (s, a1, a2, f, l) =>
        // preparing
        //   session create a feed
        //   account1 like feed
        //   session block account2
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val ids = l.map(m => await(mediumsDAO.create(m.key, m.uri, m.thumbnailUrl, m.mediumType, m.width, m.height, m.size, sessionId)))
        ids.map(i => await(mediumsDAO.find(i, accountId1.toSessionId))).flatten
        val feedId = await(feedsDAO.create(f.message, Option(ids), None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedLikesDAO.create(feedId, accountId1.toSessionId))
        await(blocksDAO.create(accountId2, sessionId))
        val result = await(feedLikesDAO.find(accountId1, None, 0, 5, accountId2.toSessionId))
        assert(result.size == 0)
      }
    }

    scenario("should not return privacy type is self") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        f.foreach({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.self, f.contentWarning, None, sessionId))
          await(feedLikesDAO.create(feedId, accountId4.toSessionId))
          f.copy(id = feedId)
        })


        // follower
        val result1 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 0)

        // not follower and not friend
        val result3 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId3.toSessionId))
        assert(result3.size == 0)


      }
    }

    scenario("should not return feeds if privacy type is followers and an account is not a follower and a friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.followers, f.contentWarning, None, sessionId))
          await(feedLikesDAO.create(feedId, accountId4.toSessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 10)
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // friend
        val result2 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId3.toSessionId))
        assert(result3.size == 0)

      }
    }

    scenario("should not return feeds when privacy type is friends and an account is not a friend") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, feed20ListGen) { (s, a1, a2, a3, a4, f) =>

        // preparing
        //  account1 is a follower.
        //  account2 is a friend.
        //  account3 is not a follower and a friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val accountId4 = await(accountsDAO.create(a4.accountName))
        await(followsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(followersDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))

        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.friends, f.contentWarning, None, sessionId))
          await(feedLikesDAO.create(feedId, accountId4.toSessionId))
          f.copy(id = feedId)
        }).reverse


        // follower
        val result1 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId1.toSessionId))
        assert(result1.size == 0)

        // friend
        val result2 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId2.toSessionId))
        assert(result2.size == 10)
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 1)
        }

        // not follower and not friend
        val result3 = await(feedLikesDAO.find(accountId4, None, 0, 10, accountId3.toSessionId))
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

          await(feedLikesDAO.create(feedId, sessionId))

          // comment a feed
          await(commentsDAO.create(feedId, c, None, sessionId))
          await(commentsDAO.create(feedId, c, None, accountId1.toSessionId))
          await(commentsDAO.create(feedId, c, None, accountId2.toSessionId))
          await(commentsDAO.create(feedId, c, None, accountId3.toSessionId))

          // block account
          await(blocksDAO.create(accountId2, accountId1.toSessionId))
          await(blocksDAO.create(accountId1, accountId3.toSessionId))

          // find by account1 and should return like count is 3
          val result1 = await(feedLikesDAO.find(sessionId.toAccountId, None, 0, 10, accountId1.toSessionId))
          assert(result1(0).id == feedId)
          assert(result1(0).likeCount == 3)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by account2 and should return like count is 3
          val result2 = await(feedLikesDAO.find(sessionId.toAccountId, None, 0, 10, accountId2.toSessionId))
          assert(result2(0).id == feedId)
          assert(result2(0).likeCount == 3)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by account2 and should return like count is 3
          val result3 = await(feedLikesDAO.find(sessionId.toAccountId, None, 0, 10, accountId4.toSessionId))
          assert(result3(0).id == feedId)
          assert(result3(0).likeCount == 4)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should not return expired feeds") {
      forOne(accountGen, accountGen, expiredFeedsGen) { (s, a, f) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, f.expiration, sessionId))
        await(feedLikesDAO.create(feedId, accountId1.toSessionId))
        val result = await(feedLikesDAO.find(accountId1, None, 0, 5, sessionId))
        assert(result.size == 0)
      }
    }

  }

}

