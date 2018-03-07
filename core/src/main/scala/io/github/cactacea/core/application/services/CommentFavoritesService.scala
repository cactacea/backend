package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.CommentFavoritesRepository
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class CommentFavoritesService {

  @Inject private var db: DatabaseService =_
  @Inject private var commentFavoritesRepository: CommentFavoritesRepository =_
  @Inject private var actionService: InjectionService =_

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- commentFavoritesRepository.create(commentId, sessionId)
        _ <- actionService.commentFavorited(commentId, sessionId)
      } yield (r)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- commentFavoritesRepository.delete(commentId, sessionId)
        _ <- actionService.commentUnFavorited(commentId, sessionId)
      } yield (r)
    }
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    commentFavoritesRepository.findAccounts(commentId, since, offset, count, sessionId)
  }

}
