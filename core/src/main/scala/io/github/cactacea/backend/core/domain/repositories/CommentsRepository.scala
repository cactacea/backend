package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.{CommentsDAO, NotificationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{CommentsValidator, FeedsValidator}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class CommentsRepository @Inject()(
                                    commentsValidator: CommentsValidator,
                                    feedsValidator: FeedsValidator,
                                    commentsDAO: CommentsDAO,
                                    notificationsDAO: NotificationsDAO
                                  ) {

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      r <- commentsDAO.find(feedId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsDAO.find(commentId, sessionId).flatMap(_ match {
      case Some(c) =>
        Future.value(c)
      case None =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      id <- commentsDAO.create(feedId, message, sessionId)
      _ <- notificationsDAO.createComment(feedId, id, sessionId)
    } yield (id)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.exist(commentId, sessionId)
      _ <- commentsDAO.delete(commentId, sessionId)
    } yield (Unit)
  }


}
