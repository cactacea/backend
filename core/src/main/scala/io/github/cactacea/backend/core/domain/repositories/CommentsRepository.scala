package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.{CommentsDAO, NotificationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{CommentsValidator, FeedsValidator}


class CommentsRepository @Inject()(
                                    commentsDAO: CommentsDAO,
                                    commentsValidator: CommentsValidator,
                                    feedsValidator: FeedsValidator,
                                    notificationsDAO: NotificationsDAO
                                  ) {

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      r <- commentsDAO.find(feedId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsValidator.find(commentId, sessionId)
  }

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      _ <- feedsValidator.exist(feedId, sessionId)
      i <- commentsDAO.create(feedId, message, sessionId)
      _ <- notificationsDAO.createComment(feedId, i, sessionId)
    } yield (i)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.exist(commentId, sessionId)
      _ <- commentsDAO.delete(commentId, sessionId)
    } yield (())
  }


}
