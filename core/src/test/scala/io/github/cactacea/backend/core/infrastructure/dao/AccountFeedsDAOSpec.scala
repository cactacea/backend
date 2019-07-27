package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec

class AccountFeedsDAOSpec extends DAOSpec {

  feature("create") {

    scenario("should fan out to followers and friends") {
      forAll(accountGen, accountGen, accountGen, accountGen, feedGen) { (s, a1, a2, a3, f) =>

        // preparing
        //  account1 is not follower and friend.
        //  account2 is follower.
        //  account3 is friend.
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followersDAO.create(accountId2, sessionId))
        await(followsDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(friendsDAO.create(accountId3, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(accountFeedsDAO.create(feedId, sessionId))

        f.privacyType match {
          case FeedPrivacyType.everyone => {

            // account1 is not follower and friend so return false
            assert(!existsAccountFeeds(feedId, accountId1))

            // account2 is a follower so return true
            assert(existsAccountFeeds(feedId, accountId2))

            // account3 is a friend so return true
            assert(existsAccountFeeds(feedId, accountId3))

          }
          case FeedPrivacyType.followers => {

            // account1 is not follower and friend so return false
            assert(!existsAccountFeeds(feedId, accountId1))

            // account2 is a follower so return true
            assert(existsAccountFeeds(feedId, accountId2))

            // account3 is a friend so return true
            assert(existsAccountFeeds(feedId, accountId3))


          }
          case FeedPrivacyType.friends => {

            // account1 is not follower and friend so return false
            assert(!existsAccountFeeds(feedId, accountId1))

            // account2 is a follower, but return false
            assert(!existsAccountFeeds(feedId, accountId2))

            // account3 is a friend so return true
            assert(existsAccountFeeds(feedId, accountId3))

          }
          case FeedPrivacyType.self => {

            // account1 is not follower and friend so return false
            assert(!existsAccountFeeds(feedId, accountId1))

            // account2 is a follower, but return false
            assert(!existsAccountFeeds(feedId, accountId2))

            // account3 is a friend, but return false
            assert(!existsAccountFeeds(feedId, accountId3))

          }
        }

      }

    }

    scenario("should not fan out if muted") {
      forOne(accountGen, accountGen, accountGen, accountGen, everyoneFeedGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  account1 is not follower and friend.
        //  account2 is follower, but muted session account.
        //  account3 is friend, but muted session account.
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followersDAO.create(accountId2, sessionId))
        await(followsDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(friendsDAO.create(accountId3, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        await(mutesDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(mutesDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(accountFeedsDAO.create(feedId, sessionId))

        // account1 is not follower and friend so return false
        assert(!existsAccountFeeds(feedId, accountId1))

        // account2 is a follower but muted so return false
        assert(!existsAccountFeeds(feedId, accountId2))

        // account3 is a friend but muted  so return false
        assert(!existsAccountFeeds(feedId, accountId3))
      }
    }

    scenario("should return an exception occurs when duplication") {
      forOne(accountGen, accountGen, accountGen, accountGen, everyoneFeedGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  account1 is not follower and friend.
        //  account2 is follower.
        //  account3 is friend.
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followersDAO.create(accountId2, sessionId))
        await(followsDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(friendsDAO.create(accountId3, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))

        // exception occurs
        await(accountFeedsDAO.create(feedId, sessionId))
        assert(intercept[ServerError] {
          await(accountFeedsDAO.create(feedId, sessionId))
        }.code == 1062)

      }
    }
  }


  feature("delete") {
    scenario("should delete account feeds") {
      forOne(accountGen, accountGen, accountGen, accountGen, everyoneFeedGen) { (s, a1, a2, a3, f) =>
        // preparing
        //  account1 is not follower and friend.
        //  account2 is follower
        //  account3 is friend
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(followersDAO.create(accountId2, sessionId))
        await(followsDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(friendsDAO.create(accountId3, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        await(mutesDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(mutesDAO.create(sessionId.toAccountId, accountId3.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(accountFeedsDAO.create(feedId, sessionId))

        await(accountFeedsDAO.delete(feedId))

        // account1 is not follower and friend so return false
        assert(!existsAccountFeeds(feedId, accountId1))

        // account2 is a follower but deleted so return false
        assert(!existsAccountFeeds(feedId, accountId2))

        // account3 is a friend but deleted  so return false
        assert(!existsAccountFeeds(feedId, accountId3))
      }
    }
  }

  feature("find") {


    scenario("should return feeds") {

      forAll(accountGen, accountGen, feed20ListGen, feedPrivacyTypeOptGen) { (s, a1, f, t) =>

        // preparing
        //  account1 is friend.
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        await(friendsDAO.create(accountId1, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId1.toSessionId))

        // create feeds and filtering by t
        val createdFeeds = f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
          await(accountFeedsDAO.create(feedId, sessionId))
          f.copy(id = feedId)
        }).filter(_.privacyType != FeedPrivacyType.self)
          .filter(f => t.fold(true)(_ == f.privacyType)).reverse

        // page1 maybe found
        val result1 = await(accountFeedsDAO.find(None, 0, 10, t, accountId1.toSessionId))
        result1.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i).id)
          assert(r.likeCount == 0)
        }

        // page2 maybe found
        val size1 = result1.size
        val result2 = await(accountFeedsDAO.find(result1.lastOption.map(_.next), 0, 10, t, accountId1.toSessionId))
        result2.zipWithIndex.map { case (r, i) =>
          assert(r.id == createdFeeds(i + size1).id)
          assert(r.likeCount == 0)
        }

        // page3 not found
        val result3 = await(accountFeedsDAO.find(result2.lastOption.map(_.next), 0, 10, t, accountId1.toSessionId))
        result3.size == 0

      }
    }

    scenario("should not return if blocked or being blocked") {
      forOne(accountGen, accountGen, accountGen, everyoneFeedGen) { (s, a1, a2, f) =>
        // preparing
        //  account1 is friend, but block session account after receive feeds.
        //  account1 is friend, but being blocked by session account after receive feeds.
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        await(friendsDAO.create(accountId1, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
        await(friendsDAO.create(accountId2, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, None, sessionId))
        await(accountFeedsDAO.create(feedId, sessionId))

        // account1 block session account
        await(blocksDAO.create(sessionId.toAccountId, accountId1.toSessionId))

        // session account block account2
        await(blocksDAO.create(accountId2, sessionId))

        // feeds found
        val result1 = await(accountFeedsDAO.find(None, 0, 10, None, accountId1.toSessionId))
        assert(result1.size == 1)

        // feeds not found
        val result2 = await(accountFeedsDAO.find(None, 0, 10, None, accountId2.toSessionId))
        assert(result2.size == 0)
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
          await(accountFeedsDAO.create(feedId, sessionId))

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
          val result1 = await(accountFeedsDAO.find(None, 0, 10, None, accountId1.toSessionId))
          assert(result1(0).id == feedId)
          assert(result1(0).likeCount == 2)
          assert(result1(0).commentCount == 3)
          assert(result1(0).liked)

          // find by account2 and should return like count is 2
          val result2 = await(accountFeedsDAO.find(None, 0, 10, None, accountId2.toSessionId))
          assert(result2(0).id == feedId)
          assert(result2(0).likeCount == 2)
          assert(result2(0).commentCount == 3)
          assert(result2(0).liked)

          // find by account2 and should return like count is 3
          val result3 = await(accountFeedsDAO.find(None, 0, 10, None, accountId4.toSessionId))
          assert(result3(0).id == feedId)
          assert(result3(0).likeCount == 3)
          assert(result3(0).commentCount == 4)
          assert(!result3(0).liked)
      }
    }

    scenario("should return mediums") {
      forOne(accountGen, accountGen, everyoneFeedGen, medium5ListGen) {
        (s, a1, f, m) =>
          // preparing
          //  account1 is friend
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          await(friendsDAO.create(accountId1, sessionId))
          await(friendsDAO.create(sessionId.toAccountId, accountId1.toSessionId))
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
          val feedId = await(feedsDAO.create(f.message, Option(mediumsId), None, f.privacyType, f.contentWarning, None, sessionId))
          await(accountFeedsDAO.create(feedId, sessionId))

          // return feed with mediums
          val result1 = await(accountFeedsDAO.find(None, 0, 10, None, accountId1.toSessionId))
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

    scenario("should not return expired feeds") {

      forAll(accountGen, accountGen, feed20ListGen, passDateTimeMillisGen) { (s, a1, f, t) =>

        // preparing
        //  account1 is friend.
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        await(friendsDAO.create(accountId1, sessionId))
        await(friendsDAO.create(sessionId.toAccountId, accountId1.toSessionId))

        // create feeds and filtering by t
        f.map({ f =>
          val feedId = await(feedsDAO.create(f.message, None, None, f.privacyType, f.contentWarning, Option(t), sessionId))
          await(accountFeedsDAO.create(feedId, sessionId))
          f.copy(id = feedId)
        }).filter(_.privacyType != FeedPrivacyType.self).reverse

        val result = await(accountFeedsDAO.find(None, 0, 10,  None, accountId1.toSessionId))
        result.size == 0

      }
    }


  }


}
