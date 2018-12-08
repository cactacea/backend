package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.{FeedPrivacyType, ReportType}
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.CommentsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{CommentNotFound, FeedNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class CommentsRepositorySpec extends RepositorySpec {

  val commentsRepository = injector.instance[CommentsRepository]
  val commentsDAO = injector.instance[CommentsDAO]
  val feedsRepository = injector.instance[FeedsRepository]
  var reportsRepository = injector.instance[ReportsRepository]

  test("create a comment") {

    val session = signUp("CommentsRepositorySpec1", "session password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    val result = execute(commentsDAO.exist(commentId, session.id.toSessionId))
    assert(result == true)

  }

  test("create a comment on no exist feed") {

    val session = signUp("CommentsRepositorySpec2", "session password", "udid")

    assert(intercept[CactaceaException] {
      execute(commentsRepository.create(FeedId(0L), "comment", session.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("delete a comment") {

    val session = signUp("CommentsRepositorySpec3", "session password", "udid")
    val user = signUp("CommentsRepositorySpec3-2", "user password", "user udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    val reportContent = Some("report content")
    execute(reportsRepository.createCommentReport(commentId, ReportType.inappropriate, reportContent, user.id.toSessionId))
    execute(commentsRepository.delete(commentId, session.id.toSessionId))
    // TODO : Check

  }

  test("delete no exist comment") {

    val session = signUp("CommentsRepositorySpec4", "session password", "udid")

    assert(intercept[CactaceaException] {
      execute(commentsRepository.delete(CommentId(0l), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("find a comment") {

    val session = signUp("CommentsRepositorySpec5", "session password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    val comment = execute(commentsRepository.find(commentId, session.id.toSessionId))
    assert(comment.id == commentId)

  }

  test("find no exist comment") {

    val session = signUp("CommentsRepositorySpec6", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(commentsRepository.find(CommentId(0L), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("find everyone comments") {

    val session = signUp("CommentsRepositorySpec7", "session password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId1 = execute(commentsRepository.create(feedId, "comment 1", session.id.toSessionId))
    val commentId2 = execute(commentsRepository.create(feedId, "comment 2", session.id.toSessionId))
    val commentId3 = execute(commentsRepository.create(feedId, "comment 3", session.id.toSessionId))
    val commentId4 = execute(commentsRepository.create(feedId, "comment 4", session.id.toSessionId))
    val commentId5 = execute(commentsRepository.create(feedId, "comment 5", session.id.toSessionId))
    val comments1 = execute(commentsRepository.findAll(feedId, None, 0, 3, session.id.toSessionId))
    val comment1 = comments1(0)
    val comment2 = comments1(1)
    val comment3 = comments1(2)
    assert(commentId5 == comment1.id)
    assert(commentId4 == comment2.id)
    assert(commentId3 == comment3.id)
    val comments2 = execute(commentsRepository.findAll(feedId, comment3.next, 0, 3, session.id.toSessionId))
    val comment4 = comments2(0)
    val comment5 = comments2(1)
    assert(commentId2 == comment4.id)
    assert(commentId1 == comment5.id)

  }

  test("find comments on no exist feed") {

    val session = signUp("CommentsRepositorySpec8", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(commentsRepository.findAll(FeedId(0L), None, 0, 3, session.id.toSessionId))
    }.error == FeedNotFound)

  }

}

