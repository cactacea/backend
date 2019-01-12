package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.CommentLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentLikesService @Inject()(
                                     db: DatabaseService,
                                     commentLikesRepository: CommentLikesRepository,
                                     actionService: ListenerService
                                   ) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- commentLikesRepository.create(commentId, sessionId)
        _ <- actionService.commentLiked(commentId, sessionId)
      } yield (Unit)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- commentLikesRepository.delete(commentId, sessionId)
        _ <- actionService.commentUnLiked(commentId, sessionId)
      } yield (Unit)
    }
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    commentLikesRepository.findAccounts(commentId, since, offset, count, sessionId)
  }

}
