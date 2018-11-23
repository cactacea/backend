package io.github.cactacea.backend.core.domain.repositories


import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.dao.CommentLikesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{CommentAlreadyLiked, CommentNotLiked, CommentNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class CommentLikesRepositorySpec extends RepositorySpec {

  val commentLikesRepository = injector.instance[CommentLikesRepository]
  val commentsRepository = injector.instance[CommentsRepository]
  val feedsRepository = injector.instance[FeedsRepository]
  val commentLikesDAO = injector.instance[CommentLikesDAO]

  test("create a comment like") {

    val session = signUp("CommentLikesRepositorySpec1", "session password", "udid")
    val user  = signUp("CommentLikesRepositorySpec2", "user password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    execute(commentLikesRepository.create(commentId, session.id.toSessionId))
    val result = execute(commentLikesDAO.exist(commentId, session.id.toSessionId))
    assert(result == true)

  }

  test("create a comment like twice") {

    val session = signUp("CommentLikesRepositorySpec3", "session password", "udid")
    val user  = signUp("CommentLikesRepositorySpec4", "user password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    execute(commentLikesRepository.create(commentId, session.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(commentLikesRepository.create(commentId, session.id.toSessionId))
    }.error == CommentAlreadyLiked)

  }

  test("create a like to no exist comment") {

    val session = signUp("CommentLikesRepositorySpec5", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(commentLikesRepository.create(CommentId(0L), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("delete a comment like") {

    val session = signUp("CommentLikesRepositorySpec6", "session password", "udid")
    val user  = signUp("CommentLikesRepositorySpec7", "user password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    execute(commentLikesRepository.create(commentId, session.id.toSessionId))
    execute(commentLikesRepository.delete(commentId, session.id.toSessionId))
    val result = execute(commentLikesDAO.exist(commentId, session.id.toSessionId))
    assert(result == false)

  }

  test("delete a comment like twice") {

    val session = signUp("CommentLikesRepositorySpec8", "session password", "udid")
    val user  = signUp("CommentLikesRepositorySpec9", "user password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    execute(commentLikesRepository.create(commentId, session.id.toSessionId))
    execute(commentLikesRepository.delete(commentId, session.id.toSessionId))

    assert(intercept[CactaceaException] {
      execute(commentLikesRepository.delete(commentId, session.id.toSessionId))
    }.error == CommentNotLiked)

  }

  test("delete a comment like to no exist comment") {

    val session = signUp("session name", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(commentLikesRepository.delete(CommentId(0L), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("find users") {

    val session = signUp("CommentLikesRepositorySpec10", "session password", "udid")
    val user1  = signUp("CommentLikesRepositorySpec11", "user1 password", "udid")
    val user2  = signUp("CommentLikesRepositorySpec12", "user2 password", "udid")
    val user3  = signUp("CommentLikesRepositorySpec13", "user3 password", "udid")
    val user4  = signUp("CommentLikesRepositorySpec14", "user4 password", "udid")
    val user5  = signUp("CommentLikesRepositorySpec15", "user5 password", "udid")
    val feedId = execute(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = execute(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    execute(commentLikesRepository.create(commentId, user1.id.toSessionId))
    execute(commentLikesRepository.create(commentId, user2.id.toSessionId))
    execute(commentLikesRepository.create(commentId, user3.id.toSessionId))
    execute(commentLikesRepository.create(commentId, user4.id.toSessionId))
    execute(commentLikesRepository.create(commentId, user5.id.toSessionId))
    val result1 = execute(commentLikesRepository.findAccounts(commentId, None, None, Some(3), session.id.toSessionId))
    val commentLike3 = result1(2)
    assert(result1.size == 3)
    val result2 = execute(commentLikesRepository.findAccounts(commentId, Some(commentLike3.next), None, Some(3), session.id.toSessionId))
    assert(result2.size == 2)

  }

  test("find no exist comment") {

    val session = signUp("CommentLikesRepositorySpec16", "session password", "udid")
    assert(intercept[CactaceaException] {
      execute(commentLikesRepository.findAccounts(CommentId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == CommentNotFound)

  }

}

