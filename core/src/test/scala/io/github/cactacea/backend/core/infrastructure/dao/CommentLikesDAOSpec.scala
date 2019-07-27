package io.github.cactacea.backend.core.infrastructure.dao

import com.twitter.finagle.mysql.ServerError
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec


class CommentLikesDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should return like count") {

      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) { (s, a1, a2, a3, f, c) =>
        // preparing
        //  session account creates a feed
        //  session account create a comment
        //  account1 like a comment
        //  account2 like a comment
        //  account3 like a comment
        //  account1 block account2
        //  account2 block account3
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        await(blocksDAO.create(accountId2, accountId1.toSessionId))
        await(blocksDAO.create(accountId1, accountId2.toSessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))
        await(commentLikesDAO.create(commentId, accountId1.toSessionId))
        await(commentLikesDAO.create(commentId, accountId2.toSessionId))
        await(commentLikesDAO.create(commentId, accountId3.toSessionId))

        // should like count 3
        val result1 = await(commentsDAO.find(commentId, sessionId))
        assert(result1.map(_.likeCount) == Option(3))

        // should like count 2 because account1 blocked account2
        val result2 = await(commentsDAO.find(commentId, accountId1.toSessionId))
        assert(result2.map(_.likeCount) == Option(2))

        // should like count 2 because account2 blocked account1
        val result3 = await(commentsDAO.find(commentId, accountId2.toSessionId))
        assert(result3.map(_.likeCount) == Option(2))
      }

    }

    scenario("should return an exception occurs when duplication") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>
        // preparing
        //  session account creates a feed
        //  session account create a comment
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // exception occurs
        await(commentLikesDAO.create(commentId, accountId1.toSessionId))
        assert(intercept[ServerError] {
          await(commentLikesDAO.create(commentId, accountId1.toSessionId))
        }.code == 1062)
      }
    }

  }


  feature("delete") {
    scenario("should delete a comment like and decrease like count") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) {
        (s, a1, a2, a3, f, c) =>
          // preparing
          //  session account creates a feed
          //  session account create a comment
          //  account1 like a comment
          //  account2 like a comment
          //  account3 like a comment
          val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
          val accountId1 = await(accountsDAO.create(a1.accountName))
          val accountId2 = await(accountsDAO.create(a2.accountName))
          val accountId3 = await(accountsDAO.create(a3.accountName))
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))
          await(commentLikesDAO.create(commentId, accountId1.toSessionId))
          await(commentLikesDAO.create(commentId, accountId2.toSessionId))
          await(commentLikesDAO.create(commentId, accountId3.toSessionId))

          await(commentLikesDAO.delete(commentId, accountId1.toSessionId))
          await(commentLikesDAO.delete(commentId, accountId2.toSessionId))
          await(commentLikesDAO.delete(commentId, accountId3.toSessionId))

          val result1 = await(commentsDAO.find(commentId, sessionId))
          assert(result1.map(_.likeCount) == Option(0))

          val result2 = await(commentsDAO.find(commentId, accountId1.toSessionId))
          assert(result2.map(_.likeCount) == Option(0))

          val result3 = await(commentsDAO.find(commentId, accountId2.toSessionId))
          assert(result3.map(_.likeCount) == Option(0))
      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) { (s, a1, a2, a3, f, c) =>

        // preparing
        //  session account creates a feed
        //  session account create a comment
        //  account1 like a comment
        //  account2 like a comment
        //  account3 like a comment
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))
        await(commentLikesDAO.create(commentId, accountId1.toSessionId))
        await(commentLikesDAO.create(commentId, accountId2.toSessionId))
        await(commentLikesDAO.create(commentId, accountId3.toSessionId))

        assert(await(commentLikesDAO.own(commentId, accountId1.toSessionId)))
        assert(await(commentLikesDAO.own(commentId, accountId2.toSessionId)))
        assert(await(commentLikesDAO.own(commentId, accountId3.toSessionId)))

        await(commentLikesDAO.delete(commentId, accountId1.toSessionId))

        assert(!await(commentLikesDAO.own(commentId, accountId1.toSessionId)))
        assert(await(commentLikesDAO.own(commentId, accountId2.toSessionId)))
        assert(await(commentLikesDAO.own(commentId, accountId3.toSessionId)))

        await(commentLikesDAO.delete(commentId, accountId2.toSessionId))

        assert(!await(commentLikesDAO.own(commentId, accountId1.toSessionId)))
        assert(!await(commentLikesDAO.own(commentId, accountId2.toSessionId)))
        assert(await(commentLikesDAO.own(commentId, accountId3.toSessionId)))

        await(commentLikesDAO.delete(commentId, accountId3.toSessionId))

        assert(!await(commentLikesDAO.own(commentId, accountId1.toSessionId)))
        assert(!await(commentLikesDAO.own(commentId, accountId2.toSessionId)))
        assert(!await(commentLikesDAO.own(commentId, accountId3.toSessionId)))
      }
    }
  }

  feature("findAccounts") {
    scenario("should return account list who liked a comment") {
      forOne(accountGen, accountGen, accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) {
        (s, a1, a2, a3, a4, a5, f, c) =>

          // preparing
          //  session account creates a feed
          //  session account create a comment
          //  account1 like a comment
          //  account2 like a comment
          //  account3 like a comment
          //  account4 like a comment
          //  account5 like a comment
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
          val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))
          await(commentLikesDAO.create(commentId, accountId1.toSessionId))
          await(commentLikesDAO.create(commentId, accountId2.toSessionId))
          await(commentLikesDAO.create(commentId, accountId3.toSessionId))
          await(commentLikesDAO.create(commentId, accountId4.toSessionId))
          await(commentLikesDAO.create(commentId, accountId5.toSessionId))

          // should return account list
          val result1 = await(commentLikesDAO.findAccounts(commentId, None, 0, 3, sessionId))
          assert(result1(0).id == accountId5)
          assert(result1(1).id == accountId4)
          assert(result1(2).id == accountId3)

          // should return next page
          val result2 = await(commentLikesDAO.findAccounts(commentId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == accountId2)
          assert(result2(1).id == accountId1)

          // should not return when account blocked
          val result3 = await(commentLikesDAO.findAccounts(commentId, None, 0, 3, accountId4.toSessionId))
          assert(result3(0).id == accountId4)
          assert(result3(1).id == accountId3)
          assert(result3(2).id == accountId2)

          // should not return when account is blocked
          val result4 = await(commentLikesDAO.findAccounts(commentId, None, 0, 3, accountId5.toSessionId))
          assert(result4(0).id == accountId5)
          assert(result4(1).id == accountId3)
          assert(result4(2).id == accountId2)

      }
    }
  }

}

