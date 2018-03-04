package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.CommentFavoritesRepository
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class CommentFavoritesService @Inject()(db: DatabaseService, commentFavoritesRepository: CommentFavoritesRepository, actionService: InjectionService) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(commentFavoritesRepository.create(commentId, sessionId))
      _ <- actionService.commentFavorited(commentId, sessionId)
    } yield (r)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(commentFavoritesRepository.delete(commentId, sessionId))
      _ <- actionService.commentUnFavorited(commentId, sessionId)
    } yield (r)
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    commentFavoritesRepository.findAccounts(commentId, since, offset, count, sessionId)
  }

}
