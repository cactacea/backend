package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.domain.repositories.CommentLikesRepository
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentLikesService {

  @Inject private var db: DatabaseService =_
  @Inject private var commentLikesRepository: CommentLikesRepository =_
  @Inject private var actionService: InjectionService =_

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- commentLikesRepository.create(commentId, sessionId)
        _ <- actionService.commentLiked(commentId, sessionId)
      } yield (r)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- commentLikesRepository.delete(commentId, sessionId)
        _ <- actionService.commentUnLiked(commentId, sessionId)
      } yield (r)
    }
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    commentLikesRepository.findAccounts(commentId, since, offset, count, sessionId)
  }

}
