package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentFavoritesRepository {

  @Inject var commentFavoritesDAO: CommentFavoritesDAO = _
  @Inject var validationRepository: ValidationDAO = _

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationRepository.existComments(commentId, sessionId)
      _ <- validationRepository.notExistCommentFavorites(commentId, sessionId)
      _ <- commentFavoritesDAO.create(commentId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationRepository.existComments(commentId, sessionId)
      _ <- validationRepository.existCommentFavorites(commentId, sessionId)
      _ <- commentFavoritesDAO.delete(commentId, sessionId)
    } yield (Future.value(Unit))
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- validationRepository.existComments(commentId, sessionId)
      r <- commentFavoritesDAO.findAll(commentId, since, offset, count, sessionId)
        .map(_.map(t => Account(t._1, t._2)))
    } yield (r)
  }


}