package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{CommentLikesValidator, CommentsValidator}

@Singleton
class CommentLikesRepository @Inject()(
                                        commentsValidator: CommentsValidator,
                                        commentLikesDAO: CommentLikesDAO,
                                        commentLikesValidator: CommentLikesValidator
                                      ) {

  def create(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.mustExist(commentId, sessionId)
      _ <- commentLikesValidator.mustNotLiked(commentId, sessionId)
      _ <- commentLikesDAO.create(commentId, sessionId)
    } yield (())
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.mustExist(commentId, sessionId)
      _ <- commentLikesValidator.mustLiked(commentId, sessionId)
      _ <- commentLikesDAO.delete(commentId, sessionId)
    } yield (())
  }

  def findUsers(commentId: CommentId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[User]] = {
    for {
      _ <- commentsValidator.mustExist(commentId, sessionId)
      r <- commentLikesDAO.findUsers(commentId, since, offset, count, sessionId)
    } yield (r)
  }

}
