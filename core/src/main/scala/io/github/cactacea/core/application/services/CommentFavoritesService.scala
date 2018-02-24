package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.CommentFavoritesRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentFavoritesService @Inject()(db: DatabaseService) {

  @Inject var commentFavoritesRepository: CommentFavoritesRepository = _

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      commentFavoritesRepository.create(commentId, sessionId)
    }
  }

  def delete(replyId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      commentFavoritesRepository.delete(replyId, sessionId)
    }
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    commentFavoritesRepository.findAccounts(commentId, since, offset, count, sessionId)
  }

}
