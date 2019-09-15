package io.github.cactacea.backend.core.domain.repositories


import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.{TweetPrivacyType, NotificationType}
import io.github.cactacea.backend.core.helpers.specs.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.CommentNotFound

class CommentsRepositorySpec extends RepositorySpec {

  feature("create") {

    scenario("should create a comment") {
      forOne(userGen, userGen, userGen, tweetGen, commentGen) { (s, a1, a2, f, c) =>

        // preparing
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId1 = await(createUser(a1.userName)).id
        val userId2 = await(createUser(a2.userName)).id
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId1 = await(commentsRepository.create(tweetId, c.message, None, userId1.sessionId))
        val commentId2 = await(commentsRepository.create(tweetId, c.message, Option(commentId1), userId2.sessionId))

        // result
        val result = await(commentsRepository.find(commentId1, sessionId))
        assert(result.id == commentId1)
        assert(result.message == c.message)
        assert(result.likeCount == 0L)

        val result2 = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), sessionId))
        assert(result2.headOption.exists(_.contentId.exists(_ == commentId1.value)))
        assert(result2.headOption.exists(_.notificationType == NotificationType.tweetReply))

        val result3 = await(notificationsRepository.find(None, 0, 10, Seq(Locale.getDefault()), userId1.sessionId))
        assert(result3.headOption.exists(_.contentId.exists(_ == commentId2.value)))
        assert(result3.headOption.exists(_.notificationType == NotificationType.commentReply))

      }
    }
  }

  feature("delete") {
    scenario("should delete a comment") {
      forOne(userGen, tweetGen, commentGen) { (s, f, c) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
        await(commentsRepository.delete(commentId, sessionId))
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
      }
    }

    scenario("should return exception if a tweet not exist") {
      forOne(userGen) { (s) =>
        val sessionId = await(usersDAO.create(s.userName)).sessionId
        assert(intercept[CactaceaException] {
          await(commentsRepository.delete(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }


    scenario("should delete comment likes") {
      forOne(userGen, userGen, tweetGen, commentGen) { (s, a, f, c) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val userId = await(createUser(a.userName)).id
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
        await(commentLikesRepository.create(commentId, userId.sessionId))
        await(commentsRepository.delete(commentId, sessionId))
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assertFutureValue(commentLikesDAO.own(commentId, userId.sessionId), false)
      }
    }

    scenario("should delete comment reports") {
      forOne(userGen, tweetGen, commentGen, commentReportGen) { (s, f, c, r) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
        val commentId = await(commentsRepository.create(tweetId, c.message, None, sessionId))
        await(commentsRepository.report(commentId, r.reportType, r.reportContent, sessionId))
        await(commentsRepository.delete(commentId, sessionId))
        assertFutureValue(commentsDAO.own(commentId, sessionId), false)
        assert(await(findCommentReport(commentId, sessionId)).isEmpty)
      }
    }

  }

  feature("find comments") {

    scenario("should return comment list") {
      forOne(userGen, tweetGen, comment20SeqGen) {
        (s, f, c) =>
          // preparing
          val sessionId = await(createUser(s.userName)).id.sessionId
          val tweetId = await(tweetsRepository.create(f.message, None, None, TweetPrivacyType.everyone, f.contentWarning, f.expiration, sessionId))
          val comments = c.map({ c =>
            val id = await(commentsRepository.create(tweetId, c.message, None, sessionId))
            c.copy(id = id)
          }).reverse

          // should return comment list
          val result = await(commentsRepository.find(tweetId, None, 0, 10, sessionId))
          result.zipWithIndex.foreach({ case (c, i) =>
            assert(c.id == comments(i).id)
          })
      }
    }

  }

  feature("find a comment") {

    scenario("should return exception if a tweet not exist") {
      forOne(userGen) { (s) =>
        val sessionId = await(createUser(s.userName)).id.sessionId
        assert(intercept[CactaceaException] {
          await(commentsRepository.find(CommentId(0), sessionId))
        }.error == CommentNotFound)
      }
    }

  }

}