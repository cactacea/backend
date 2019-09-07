package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.{CommentReportsDAO, CommentsDAO, NotificationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{CommentsValidator, FeedsValidator}

@Singleton
class CommentsRepository @Inject()(
                                    commentsDAO: CommentsDAO,
                                    commentReportsDAO: CommentReportsDAO,
                                    commentsValidator: CommentsValidator,
                                    feedsValidator: FeedsValidator,
                                    notificationsDAO: NotificationsDAO
                                  ) {

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Comment]] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      r <- commentsDAO.find(feedId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsValidator.mustFind(commentId, sessionId)
  }

  def create(feedId: FeedId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    for {
      _ <- feedsValidator.mustExist(feedId, sessionId)
      a <- feedsValidator.mustFindOwner(feedId, replyId)
      _ <- commentsValidator.mustExist(feedId, replyId, sessionId)
      i <- commentsDAO.create(feedId, message, replyId, sessionId)
      _ <- notificationsDAO.create(feedId, i, a, replyId.isDefined, sessionId)
    } yield (i)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- commentsValidator.mustFind(commentId, sessionId)
      _ <- commentsDAO.delete(c.feedId, commentId, sessionId)
    } yield (())
  }

  def report(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.mustExist(commentId, sessionId)
      _ <- commentReportsDAO.create(commentId, reportType, reportContent, sessionId)
    } yield (())
  }

}
