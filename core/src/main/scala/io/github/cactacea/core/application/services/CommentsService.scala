package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, PublishService}
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.domain.models.Comment
import io.github.cactacea.core.domain.repositories.{CommentsRepository, ReportsRepository}
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class CommentsService @Inject()(db: DatabaseService, commentsRepository: CommentsRepository, reportsRepository: ReportsRepository, publishService: PublishService, actionService: InjectionService) {

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      id <- db.transaction(commentsRepository.create(feedId, message, sessionId))
      _ <- publishService.enqueueComment(id)
      _ <- actionService.commentCreated(id, sessionId)
    } yield (id)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(commentsRepository.delete(commentId, sessionId))
      _ <- actionService.commentDeleted(commentId, sessionId)
    } yield (r)
  }

  def findAll(feedId: FeedId, since: Option[Long], count: Option[Int], sessionId: SessionId): Future[List[Comment]] = {
    commentsRepository.findAll(feedId, since, count, sessionId)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsRepository.find(
      commentId,
      sessionId
    )
  }

  def report(commentId: CommentId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    for {
      r <- db.transaction(reportsRepository.createCommentReport(commentId, reportType, sessionId))
      _ <- actionService.commentReported(commentId, reportType, sessionId)
    } yield (r)
  }

}



