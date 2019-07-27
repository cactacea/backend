package io.github.cactacea.backend.core.infrastructure.dao

import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.DAOSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId

class CommentsDAOSpec extends DAOSpec {

  feature("create") {
    scenario("should create a comment") {
      forAll(accountGen, accountGen, accountGen, feedGen, comment20ListGen) { (s, a1, a2, f, c) =>

        // preparing
        //  account1 create a feed
        //  account2 create a comment
        //  account1 create a comment
        //  account2 create a comment
        //  account1 create a comment
        //  account2 create a comment
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, accountId1.toSessionId))
        val commentId1 = await(commentsDAO.create(feedId, c(0).message, None, accountId2.toSessionId))
        val commentId2 = await(commentsDAO.create(feedId, c(1).message, None, accountId1.toSessionId))
        val commentId3 = await(commentsDAO.create(feedId, c(2).message, None, accountId2.toSessionId))
        val commentId4 = await(commentsDAO.create(feedId, c(3).message, None, accountId1.toSessionId))
        val commentId5 = await(commentsDAO.create(feedId, c(4).message, None, accountId2.toSessionId))

        // return comment1 exist
        // return comment2 exist
        // return comment3 exist
        // return comment4 exist
        // return comment5 exist
        assert(await(commentsDAO.own(commentId1, accountId2.toSessionId)))
        assert(await(commentsDAO.own(commentId2, accountId1.toSessionId)))
        assert(await(commentsDAO.own(commentId3, accountId2.toSessionId)))
        assert(await(commentsDAO.own(commentId4, accountId1.toSessionId)))
        assert(await(commentsDAO.own(commentId5, accountId2.toSessionId)))

        // return comment count 5
        val feed = await(feedsDAO.find(feedId, sessionId))
        assert(feed.map(_.commentCount) == Option(5L))
      }
    }
  }

  feature("delete") {
    scenario("should delete a comment") {
      forOne(accountGen, accountGen, accountGen, feedGen, comment20ListGen) { (s, a1, a2, f, c) =>

        // preparing
        //  account1 create a feed
        //  account2 create and delete a comment
        //  account1 create and delete a comment
        //  account2 create and delete a comment
        //  account1 create and delete a comment
        //  account2 create and delete a comment
        await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, accountId1.toSessionId))
        val commentId1 = await(commentsDAO.create(feedId, c(0).message, None, accountId2.toSessionId))
        val commentId2 = await(commentsDAO.create(feedId, c(1).message, None, accountId1.toSessionId))
        val commentId3 = await(commentsDAO.create(feedId, c(2).message, None, accountId2.toSessionId))
        val commentId4 = await(commentsDAO.create(feedId, c(3).message, None, accountId1.toSessionId))
        val commentId5 = await(commentsDAO.create(feedId, c(4).message, None, accountId2.toSessionId))
        await(commentsDAO.delete(feedId, commentId1, accountId2.toSessionId))
        await(commentsDAO.delete(feedId, commentId2, accountId1.toSessionId))
        await(commentsDAO.delete(feedId, commentId3, accountId2.toSessionId))
        await(commentsDAO.delete(feedId, commentId4, accountId1.toSessionId))
        await(commentsDAO.delete(feedId, commentId5, accountId2.toSessionId))

        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        assert(!await(commentsDAO.own(commentId1, accountId2.toSessionId)))
        assert(!await(commentsDAO.own(commentId2, accountId1.toSessionId)))
        assert(!await(commentsDAO.own(commentId3, accountId2.toSessionId)))
        assert(!await(commentsDAO.own(commentId4, accountId1.toSessionId)))
        assert(!await(commentsDAO.own(commentId5, accountId2.toSessionId)))

        // return comment count 0
        val feed = await(feedsDAO.find(feedId, accountId1.toSessionId))
        assert(feed.exists(_.commentCount == 0L))
      }
    }

    scenario("should delete all comments") {
      forOne(accountGen, accountGen, accountGen, feedGen, comment20ListGen) { (s, a1, a2, f, c) =>

        // preparing
        //  account1 create a feed
        //  account2 create and delete a comment
        //  account1 create and delete a comment
        //  account2 create and delete a comment
        //  account1 create and delete a comment
        //  account2 create and delete a comment
        await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, accountId1.toSessionId))
        val commentId1 = await(commentsDAO.create(feedId, c(0).message, None, accountId2.toSessionId))
        val commentId2 = await(commentsDAO.create(feedId, c(1).message, None, accountId1.toSessionId))
        val commentId3 = await(commentsDAO.create(feedId, c(2).message, None, accountId2.toSessionId))
        val commentId4 = await(commentsDAO.create(feedId, c(3).message, None, accountId1.toSessionId))
        val commentId5 = await(commentsDAO.create(feedId, c(4).message, None, accountId2.toSessionId))
        await(feedsDAO.delete(feedId, accountId1.toSessionId))

        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        // return false because of deleted
        assert(!await(commentsDAO.own(commentId1, accountId2.toSessionId)))
        assert(!await(commentsDAO.own(commentId2, accountId1.toSessionId)))
        assert(!await(commentsDAO.own(commentId3, accountId2.toSessionId)))
        assert(!await(commentsDAO.own(commentId4, accountId1.toSessionId)))
        assert(!await(commentsDAO.own(commentId5, accountId2.toSessionId)))

      }
    }

  }

  feature("exists") {

    scenario("should return a comment exist or not") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>

        // preparing
        //  session account block account1
        //  session create a feed
        //  session create a comment on the feed
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        await(blocksDAO.create(accountId1, sessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // return exist by session account
        // return not exist by account1 because account1 be blocked by session account
        // return no exist comment
        assert(await(commentsDAO.exists(commentId, sessionId)))
        assert(!await(commentsDAO.exists(commentId, accountId1.toSessionId)))
        assert(!await(commentsDAO.exists(CommentId(0L), sessionId)))

        // return no exist because a comment is deleted
        await(commentsDAO.delete(feedId, commentId, sessionId))
        assert(!await(commentsDAO.exists(commentId, sessionId)))
      }
    }

    scenario("should return a feed comment exist or not") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>

        // preparing
        //  session account block account1
        //  session create a feed
        //  session create a comment on the feed
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        await(blocksDAO.create(accountId1, sessionId))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // return exist by session account
        // return not exist by account1 because account1 be blocked by session account
        // return no exist comment
        assert(await(commentsDAO.exists(feedId, commentId, sessionId)))
        assert(!await(commentsDAO.exists(feedId, commentId, accountId1.toSessionId)))
        assert(!await(commentsDAO.exists(feedId, CommentId(0L), sessionId)))

        // return no exist because a comment is deleted
        await(commentsDAO.delete(feedId, commentId, sessionId))
        assert(!await(commentsDAO.exists(feedId, commentId, sessionId)))
      }
    }

  }

  feature("own") {
    scenario("should return owner or not") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (a1, a2, f, c) =>

        // preparing
        //  session account block account1
        //  session create a feed
        //  session create a comment on the feed
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val accountId = await(accountsDAO.create(a2.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        // return true because of owner
        // return false because of not owner
        assert(await(commentsDAO.own(commentId, sessionId)))
        assert(!await(commentsDAO.own(commentId, accountId.toSessionId)))

        // return false because of deleted
        await(commentsDAO.delete(feedId, commentId, sessionId))
        assert(!await(commentsDAO.own(commentId, sessionId)))
      }
    }
  }

  feature("findOwner") {
    scenario("should return owner") {
      forOne(accountGen, feedGen, commentGen) { (a1, f, c) =>

        // preparing
        val sessionId = await(accountsDAO.create(a1.accountName)).toSessionId
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        assertFutureValue(commentsDAO.findOwner(commentId), Option(sessionId.toAccountId))
      }
    }
  }

  feature("find by commentId") {
    scenario("should return a comment") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) { (s, a1, a2, a3, f, c) =>

        // preparing
        //  session account create a feed
        //  session account create a comment
        //  session account block account1
        //  session account2 block session account
        //  account1 like a comment
        //  account2 like a comment
        //  account3 like a comment
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        val accountId1 = await(accountsDAO.create(a1.accountName))
        val accountId2 = await(accountsDAO.create(a2.accountName))
        val accountId3 = await(accountsDAO.create(a3.accountName))
        val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsDAO.create(feedId, c.message, None, sessionId))

        await(blocksDAO.create(accountId1, sessionId))
        await(blocksDAO.create(sessionId.toAccountId, accountId2.toSessionId))
        await(blocksDAO.create(accountId3, accountId1.toSessionId))

        await(commentLikesDAO.create(commentId, accountId1.toSessionId))
        await(commentLikesDAO.create(commentId, accountId2.toSessionId))
        await(commentLikesDAO.create(commentId, accountId3.toSessionId))

        // should return a comment and like count
        val result1 = await(commentsDAO.find(commentId, sessionId))
        assert(result1.isDefined)
        assert(result1.map(_.id) == Option(commentId))
        assert(result1.map(_.message) == Option(c.message))
        assert(result1.map(_.likeCount) == Option(2L))

        // should not return when account is blocked
        val result2 = await(commentsDAO.find(commentId, accountId1.toSessionId))
        assert(result2.isEmpty)

        // should return when account blocked
        val result3 = await(commentsDAO.find(commentId, accountId2.toSessionId))
        assert(result3.isDefined)
        assert(result3.map(_.id) == Option(commentId))
        assert(result3.map(_.message) == Option(c.message))
        assert(result3.map(_.likeCount) == Option(3L))

        // should return like count but ignore blocked and be blocked account like count
        val result4 = await(commentsDAO.find(commentId, accountId3.toSessionId))
        assert(result4.isDefined)
        assert(result4.map(_.id) == Option(commentId))
        assert(result4.map(_.message) == Option(c.message))
        assert(result4.map(_.likeCount) == Option(2L))

      }
    }
  }

  feature("find") {

    scenario("should return comment list") {
      forOne(accountGen, accountGen, accountGen, feedGen, comment20ListGen) {
        (s, a1, a2, f, c) =>

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
          await(blocksDAO.create(accountId2, sessionId))
          await(blocksDAO.create(sessionId.toAccountId, accountId1.toSessionId))
          val feedId = await(feedsDAO.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val comments = c.map({c =>
            val id = await(commentsDAO.create(feedId, c.message, None, sessionId))
            c.copy(id = id)
          }).reverse

          // should return comment list
          val result1 = await(commentsDAO.find(feedId, None, 0, 10, sessionId))
          result1.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
          })

          // should return next page
          val size2 = result1.size
          val result2 = await(commentsDAO.find(feedId, result1.lastOption.map(_.next), 0, 10, sessionId))
          result2.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i + size2).id)
          })

          // should not return when account blocked
          assert(await(commentsDAO.find(feedId, None, 0, 10, accountId1.toSessionId)).nonEmpty)

          // should not return when account is blocked
          assert(await(commentsDAO.find(feedId, None, 0, 10, accountId2.toSessionId)).isEmpty)

      }
    }


    scenario("should return like count") {
      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, comment20ListGen) {
        (s, a1, a2, a3, f, c) =>
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
          val comments = c.map({c =>
            val id = await(commentsDAO.create(feedId, c.message, None, sessionId))
            await(commentLikesDAO.create(id, accountId1.toSessionId))
            await(commentLikesDAO.create(id, accountId2.toSessionId))
            await(commentLikesDAO.create(id, accountId3.toSessionId))
            c.copy(id = id)
          }).reverse

          // should like count 3
          val result1 = await(commentsDAO.find(feedId, None, 0, 10, sessionId))
          result1.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
            assert(c.likeCount == 3)
          })

          // should like count 2 because account1 blocked account2
          val result2 = await(commentsDAO.find(feedId, None, 0, 10, accountId1.toSessionId))
          result2.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
            assert(c.likeCount == 2)
          })

          // should like count 2 because account2 blocked account1
          val result3 = await(commentsDAO.find(feedId, None, 0, 10, accountId2.toSessionId))
          result3.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
            assert(c.likeCount == 2)
          })
      }
    }

  }

}

