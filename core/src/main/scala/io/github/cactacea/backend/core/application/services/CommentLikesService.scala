package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.CommentLikesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentLikesService @Inject()(
                                     commentLikesRepository: CommentLikesRepository,
                                     databaseService: DatabaseService
                                   ) {

  import databaseService._

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    transaction {
      commentLikesRepository.create(commentId, sessionId)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    transaction {
      commentLikesRepository.delete(commentId, sessionId)
    }
  }

  def findUsers(commentId: CommentId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[User]] = {
    commentLikesRepository.findUsers(commentId, since, offset, count, sessionId)
  }

}
