package io.github.cactacea.backend.core.domain.repositories


import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, NotificationType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.CommentNotFound

class CommentsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a comment") {
      forOne(accountGen, accountGen, accountGen, feedGen, commentGen) { (s, a1, a2, f, c) =>

        // preparing
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId1 = await(accountsRepository.create(a1.accountName)).id
        val accountId2 = await(accountsRepository.create(a2.accountName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId1 = await(commentsRepository.create(feedId, c.message, None, accountId1.toSessionId))
        val commentId2 = await(commentsRepository.create(feedId, c.message, Option(commentId1), accountId2.toSessionId))

        // result
        val result = await(commentsRepository.find(commentId1, sessionId))
        assert(result.id == commentId1)
        assert(result.message == c.message)
        assert(result.likeCount == 0L)

        val result2 = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), sessionId))
        assert(result2.headOption.exists(_.contentId.exists(_ == commentId1.value)))
        assert(result2.headOption.exists(_.notificationType == NotificationType.feedReply))

        val result3 = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), accountId1.toSessionId))
        assert(result3.headOption.exists(_.contentId.exists(_ == commentId2.value)))
        assert(result3.headOption.exists(_.notificationType == NotificationType.commentReply))

      }
    }
  }

  feature("delete") {
    scenario("should delete a comment") {
      forOne(accountGen, feedGen, commentGen) { (s, f, c) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
        await(commentsRepository.delete(commentId, sessionId))
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
      }
    }

    scenario("should return exception if a feed not exist") {
      forOne(accountGen) { (s) =>
        val sessionId = await(accountsDAO.create(s.accountName)).toSessionId
        assert(intercept[CactaceaException] {
          await(commentsRepository.delete(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }


    scenario("should delete comment likes") {
      forOne(accountGen, accountGen, feedGen, commentGen) { (s, a, f, c) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val accountId = await(accountsRepository.create(a.accountName)).id
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
        await(commentLikesRepository.create(commentId, accountId.toSessionId))
        await(commentsRepository.delete(commentId, sessionId))
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assertFutureValue(commentLikesDAO.own(commentId, accountId.toSessionId), false)
      }
    }

    scenario("should delete comment reports") {
      forOne(accountGen, feedGen, commentGen, commentReportGen) { (s, f, c, r) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(feedId, c.message, None, sessionId))
        await(commentsRepository.report(commentId, r.reportType, r.reportContent, sessionId))
        await(commentsRepository.delete(commentId, sessionId))
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assert(await(findCommentReport(commentId, sessionId)).isEmpty)
      }
    }

  }

  feature("find comments") {

    scenario("should return comment list") {
      forOne(accountGen, feedGen, comment20ListGen) {
        (s, f, c) =>
          // preparing
          val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
          val feedId = await(feedsRepository.create(f.message, None, None, FeedPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val comments = c.map({ c =>
            val id = await(commentsRepository.create(feedId, c.message, None, sessionId))
            c.copy(id = id)
          }).reverse

          // should return comment list
          val result = await(commentsRepository.find(feedId, None, 0, 10, sessionId))
          result.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
          })
      }
    }

  }

  feature("find a comment") {

    scenario("should return exception if a feed not exist") {
      forOne(accountGen) { (s) =>
        val sessionId = await(accountsRepository.create(s.accountName)).id.toSessionId
        assert(intercept[CactaceaException] {
          await(commentsRepository.find(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }

  }

}