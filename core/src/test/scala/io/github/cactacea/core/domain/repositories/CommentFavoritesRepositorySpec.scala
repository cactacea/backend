package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.RepositorySpec
import io.github.cactacea.core.infrastructure.dao.CommentFavoritesDAO
import io.github.cactacea.core.infrastructure.identifiers.CommentId
import io.github.cactacea.core.util.responses.CactaceaError.{CommentAlreadyFavorited, CommentNotFavorited, CommentNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class CommentFavoritesRepositorySpec extends RepositorySpec {

  val commentFavoritesRepository = injector.instance[CommentFavoritesRepository]
  val commentsRepository = injector.instance[CommentsRepository]
  val feedsRepository = injector.instance[FeedsRepository]
  val commentFavoritesDAO = injector.instance[CommentFavoritesDAO]

  test("create a comment favorite") {

    val session = signUp("session name", "session password", "udid").account
    val user  = signUp("user name", "user password", "udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, session.id.toSessionId))
    val result = Await.result(commentFavoritesDAO.exist(commentId, session.id.toSessionId))
    assert(result == true)

  }

  test("create a comment favorite twice") {

    val session = signUp("session name", "session password", "udid").account
    val user  = signUp("user name", "user password", "udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, session.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(commentFavoritesRepository.create(commentId, session.id.toSessionId))
    }.error == CommentAlreadyFavorited)

  }

  test("create a favorite to no exist comment") {

    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(commentFavoritesRepository.create(CommentId(0L), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("delete a comment favorite") {

    val session = signUp("session name", "session password", "udid").account
    val user  = signUp("user name", "user password", "udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, session.id.toSessionId))
    Await.result(commentFavoritesRepository.delete(commentId, session.id.toSessionId))
    val result = Await.result(commentFavoritesDAO.exist(commentId, session.id.toSessionId))
    assert(result == false)

  }

  test("delete a comment favorite twice") {

    val session = signUp("session name", "session password", "udid").account
    val user  = signUp("user name", "user password", "udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", user.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, session.id.toSessionId))
    Await.result(commentFavoritesRepository.delete(commentId, session.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(commentFavoritesRepository.delete(commentId, session.id.toSessionId))
    }.error == CommentNotFavorited)

  }

  test("delete a comment favorite to no exist comment") {

    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(commentFavoritesRepository.delete(CommentId(0L), session.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("find users") {

    val session = signUp("session name", "session password", "udid").account
    val user1  = signUp("user1 name", "user1 password", "udid").account
    val user2  = signUp("user2 name", "user2 password", "udid").account
    val user3  = signUp("user3 name", "user3 password", "udid").account
    val user4  = signUp("user4 name", "user4 password", "udid").account
    val user5  = signUp("user5 name", "user5 password", "udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, user1.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, user2.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, user3.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, user4.id.toSessionId))
    Await.result(commentFavoritesRepository.create(commentId, user5.id.toSessionId))
    val result1 = Await.result(commentFavoritesRepository.findAccounts(commentId, None, None, Some(3), session.id.toSessionId))
    val commentFavorite3 = result1(2)
    assert(result1.size == 3)
    val result2 = Await.result(commentFavoritesRepository.findAccounts(commentId, Some(commentFavorite3.next), None, Some(3), session.id.toSessionId))
    assert(result2.size == 2)

  }

  test("find no exist comment") {

    val session = signUp("session name", "session password", "udid").account
    assert(intercept[CactaceaException] {
      Await.result(commentFavoritesRepository.findAccounts(CommentId(0L), None, None, Some(3), session.id.toSessionId))
    }.error == CommentNotFound)

  }

}

