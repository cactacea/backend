package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.domain.repositories.CommentLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}

class CommentLikesService @Inject()(
                                     db: DatabaseService,
                                     commentLikesRepository: CommentLikesRepository
                                   ) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(commentLikesRepository.create(commentId, sessionId))
    } yield (())
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(commentLikesRepository.delete(commentId, sessionId))
    } yield (())
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    commentLikesRepository.findAccounts(commentId, since, offset, count, sessionId)
  }

}
