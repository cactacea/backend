package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{CommentLikesValidator, CommentsValidator}


class CommentLikesRepository @Inject()(
                                        commentsValidator: CommentsValidator,
                                        commentLikesValidator: CommentLikesValidator,
                                        commentLikesDAO: CommentLikesDAO,
                                      ) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.exist(commentId, sessionId)
      _ <- commentLikesValidator.notExist(commentId, sessionId)
      _ <- commentLikesDAO.create(commentId, sessionId)
    } yield (())
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.exist(commentId, sessionId)
      _ <- commentLikesValidator.exist(commentId, sessionId)
      _ <- commentLikesDAO.delete(commentId, sessionId)
    } yield (())
  }

  def findAccounts(commentId: CommentId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- commentsValidator.exist(commentId, sessionId)
      r <- commentLikesDAO.find(commentId, since, offset, count, sessionId)
    } yield (r)
  }

}
