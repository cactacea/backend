package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{CommentAlreadyLiked, CommentNotFound, CommentNotLiked}

class CommentLikesRepositorySpec extends RepositorySpec {


  feature("create") {
    scenario("should create a comment liked") {

      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) { (s, a1, a2, a3, f, c) =>
        // preparing
        //  session account creates a feed
        //  session account create a comment
        //  account1 like a comment
        //  account2 like a comment
        //  account3 like a comment
        //  account1 block account2
        //  account2 block account3
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val accountId3 = await(accountsRepository.create(a3.accountName)).id
        await(blocksRepository.create(accountId2, accountId1.toSessionId))
        await(blocksRepository.create(accountId1, accountId2.toSessionId))
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
        await(commentLikesRepository.create(commentId, accountId1.toSessionId))
        await(commentLikesRepository.create(commentId, accountId2.toSessionId))
        await(commentLikesRepository.create(commentId, accountId3.toSessionId))

        // should like count 3
        val result1 = await(commentsRepository.find(commentId, sessionId))
        assert(result1.likeCount == 3)

        // should like count 2 because account1 blocked account2
        val result2 = await(commentsRepository.find(commentId, accountId1.toSessionId))
        assert(result2.likeCount == 2)

        // should like count 2 because account2 blocked account1
        val result3 = await(commentsRepository.find(commentId, accountId2.toSessionId))
        assert(result3.likeCount == 2)
      }

    }

    scenario("should return exception if a comment already liked") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>
        // preparing
        //  session account creates a feed
        //  session account create a comment
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))

        // exception occurs
        await(commentLikesRepository.create(commentId, accountId1.toSessionId))
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.create(commentId, accountId1.toSessionId))
        }.error == CommentAlreadyLiked)
      }
    }

    scenario("should return exception if a comment not exist") {
      forOne(accountGen) { (s) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.create(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }

  }


  feature("delete") {

    scenario("should delete a comment liked") {

      forOne(accountGen, accountGen, accountGen, accountGen, feedGen, commentGen) {
        (s, a1, a2, a3, f, c) =>
          // preparing
          //  session account creates a feed
          //  session account create a comment
          //  account1 like a comment
          //  account2 like a comment
          //  account3 like a comment
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val accountId2 = await(accountsRepository.create(a2.accountName)).id
          val accountId3 = await(accountsRepository.create(a3.accountName)).id
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
          await(commentLikesRepository.create(commentId, accountId1.toSessionId))
          await(commentLikesRepository.create(commentId, accountId2.toSessionId))
          await(commentLikesRepository.create(commentId, accountId3.toSessionId))

          await(commentLikesRepository.delete(commentId, accountId1.toSessionId))
          await(commentLikesRepository.delete(commentId, accountId2.toSessionId))
          await(commentLikesRepository.delete(commentId, accountId3.toSessionId))

          assertFutureValue(commentLikesDAO.own(commentId, sessionId), false)

      }
    }

    scenario("should return exception if a comment not liked") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a1, f, c) =>
        // preparing
        //  session account creates a feed
        //  session account create a comment
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))

        // exception occurs
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.delete(commentId, accountId1.toSessionId))
        }.error == CommentNotLiked)
      }
    }

    scenario("should return exception if a comment not exist") {
      forOne(accountGen) { (s) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.delete(CommentId(0), sessionId))
        }.error == CommentNotFound)
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
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val accountId1 = await(accountsRepository.create(a1.accountName)).id
          val accountId2 = await(accountsRepository.create(a2.accountName)).id
          val accountId3 = await(accountsRepository.create(a3.accountName)).id
          val accountId4 = await(accountsRepository.create(a4.accountName)).id
          val accountId5 = await(accountsRepository.create(a5.accountName)).id
          await(blocksRepository.create(accountId4, accountId5.toSessionId))
          await(blocksRepository.create(accountId5, accountId4.toSessionId))

          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
          await(commentLikesRepository.create(commentId, accountId1.toSessionId))
          await(commentLikesRepository.create(commentId, accountId2.toSessionId))
          await(commentLikesRepository.create(commentId, accountId3.toSessionId))
          await(commentLikesRepository.create(commentId, accountId4.toSessionId))
          await(commentLikesRepository.create(commentId, accountId5.toSessionId))

          // should return account list
          val result1 = await(commentLikesRepository.findAccounts(commentId, None, 0, 3, sessionId))
          assert(result1(0).id == accountId5)
          assert(result1(1).id == accountId4)
          assert(result1(2).id == accountId3)

          // should return next page
          val result2 = await(commentLikesRepository.findAccounts(commentId, result1.lastOption.map(_.next), 0, 3, sessionId))
          assert(result2(0).id == accountId2)
          assert(result2(1).id == accountId1)

          // should not return when account blocked
          val result3 = await(commentLikesRepository.findAccounts(commentId, None, 0, 3, accountId4.toSessionId))
          assert(result3(0).id == accountId4)
          assert(result3(1).id == accountId3)
          assert(result3(2).id == accountId2)

          // should not return when account is blocked
          val result4 = await(commentLikesRepository.findAccounts(commentId, None, 0, 3, accountId5.toSessionId))
          assert(result4(0).id == accountId5)
          assert(result4(1).id == accountId3)
          assert(result4(2).id == accountId2)

      }
    }

    scenario("should return exception if a comment not exist") {
      forOne(accountGen) { (s) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        assert(intercept[CactaceaException] {
          await(commentLikesRepository.findAccounts(CommentId(0), None, 0, 3, sessionId))
        }.error == CommentNotFound)
      }
    }

  }

}
