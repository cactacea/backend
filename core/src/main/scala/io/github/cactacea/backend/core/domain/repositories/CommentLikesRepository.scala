package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}

@Singleton
class CommentLikesRepository @Inject()(
                                        commentsDAO: CommentsDAO,
                                        commentLikesDAO: CommentLikesDAO
                                      ) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsDAO.validateExist(commentId, sessionId)
      _ <- commentLikesDAO.validateNotExist(commentId, sessionId)
      _ <- commentLikesDAO.create(commentId, sessionId)
    } yield (Unit)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsDAO.validateExist(commentId, sessionId)
      _ <- commentLikesDAO.validateExist(commentId, sessionId)
      _ <- commentLikesDAO.delete(commentId, sessionId)
    } yield (Unit)
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- commentsDAO.validateExist(commentId, sessionId)
      r <- commentLikesDAO.find(commentId, since, offset, count, sessionId)
    } yield (r)
  }

}
