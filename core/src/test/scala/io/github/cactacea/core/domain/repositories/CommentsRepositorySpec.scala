package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.CommentsDAO
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, FeedId}
import io.github.cactacea.core.util.responses.CactaceaErrors.{CommentNotFound, FeedNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class CommentsRepositorySpec extends RepositorySpec {

  val commentsRepository = injector.instance[CommentsRepository]
  val commentsDAO = injector.instance[CommentsDAO]
  val feedsRepository = injector.instance[FeedsRepository]
  var reportsRepository = injector.instance[ReportsRepository]

  test("create a comment") {

    val session = signUp("session name", "session password", "udid")
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    val result = Await.result(commentsDAO.exist(commentId, session.id.toSessionId))
    assert(result == true)

  }

  test("create a comment on no exist feed") {

    val session = signUp("session name", "session password", "udid")

    assert(intercept[CactaceaException] {
      Await.result(commentsRepository.create(FeedId(0L), "comment", session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("delete a comment") {

    val session = signUp("session name", "session password", "udid")
    val user = signUp("user name", "user password", "user udid")
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    Await.result(reportsRepository.createCommentReport(commentId, ReportType.inappropriate, user.id.toSessionId))
    val result = Await.result(commentsRepository.delete(commentId, session.id.toSessionId))
    // TODO : Check

  }

  test("delete no exist comment") {

    val session = signUp("session name", "session password", "udid")

    assert(intercept[CactaceaException] {
      Await.result(commentsRepository.delete(CommentId(0l), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("find a comment") {

    val session = signUp("session name", "session password", "udid")
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    val comment = Await.result(commentsRepository.find(commentId, session.id.toSessionId))
    assert(comment.id == commentId)

  }

  test("find no exist comment") {

    val session = signUp("session name", "session password", "udid")
    assert(intercept[CactaceaException] {
      Await.result(commentsRepository.find(CommentId(0L), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("find everyone comments") {

    val session = signUp("session name", "session password", "udid")
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId1 = Await.result(commentsRepository.create(feedId, "comment 1", session.id.toSessionId))
    val commentId2 = Await.result(commentsRepository.create(feedId, "comment 2", session.id.toSessionId))
    val commentId3 = Await.result(commentsRepository.create(feedId, "comment 3", session.id.toSessionId))
    val commentId4 = Await.result(commentsRepository.create(feedId, "comment 4", session.id.toSessionId))
    val commentId5 = Await.result(commentsRepository.create(feedId, "comment 5", session.id.toSessionId))
    val comments1 = Await.result(commentsRepository.findAll(feedId, None, Some(3), session.id.toSessionId))
    val comment1 = comments1(0)
    val comment2 = comments1(1)
    val comment3 = comments1(2)
    assert(commentId5 == comment1.id)
    assert(commentId4 == comment2.id)
    assert(commentId3 == comment3.id)
    val comments2 = Await.result(commentsRepository.findAll(feedId, Some(comment3.next), Some(3), session.id.toSessionId))
    val comment4 = comments2(0)
    val comment5 = comments2(1)
    assert(commentId2 == comment4.id)
    assert(commentId1 == comment5.id)

  }

  test("find comments on no exist feed") {

    val session = signUp("session name", "session password", "udid")
    assert(intercept[CactaceaException] {
      Await.result(commentsRepository.findAll(FeedId(0L), None, Some(3), session.id.toSessionId))
    }.error == FeedNotFound)

  }

}

