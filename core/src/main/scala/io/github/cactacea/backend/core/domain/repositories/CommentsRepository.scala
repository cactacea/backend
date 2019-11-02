package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.infrastructure.dao.{CommentReportsDAO, CommentsDAO, InformationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, TweetId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{CommentsValidator, TweetsValidator}

@Singleton
class CommentsRepository @Inject()(
                                    commentsDAO: CommentsDAO,
                                    commentReportsDAO: CommentReportsDAO,
                                    commentsValidator: CommentsValidator,
                                    tweetsValidator: TweetsValidator,
                                    notificationsDAO: InformationsDAO
                                  ) {

  def find(tweetId: TweetId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Comment]] = {
    for {
      _ <- tweetsValidator.mustExist(tweetId, sessionId)
      r <- commentsDAO.find(tweetId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsValidator.mustFind(commentId, sessionId)
  }

  def create(tweetId: TweetId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    for {
      _ <- tweetsValidator.mustExist(tweetId, sessionId)
      a <- tweetsValidator.mustFindOwner(tweetId, replyId)
      _ <- commentsValidator.mustExist(tweetId, replyId, sessionId)
      i <- commentsDAO.create(tweetId, message, replyId, sessionId)
      _ <- notificationsDAO.create(tweetId, i, a, replyId.isDefined, sessionId)
    } yield (i)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      c <- commentsValidator.mustFind(commentId, sessionId)
      _ <- commentsDAO.delete(c.tweetId, commentId, sessionId)
    } yield (())
  }

  def report(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsValidator.mustExist(commentId, sessionId)
      _ <- commentReportsDAO.create(commentId, reportType, reportContent, sessionId)
    } yield (())
  }

}
