package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{QueueService, ListenerService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.domain.repositories.{CommentsRepository, ReportsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}

@Singleton
class CommentsService @Inject()(
                                 db: DatabaseService,
                                 commentsRepository: CommentsRepository,
                                 reportsRepository: ReportsRepository,
                                 queueService: QueueService,
                                 listenerService: ListenerService
                               ) {

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      id <- db.transaction(commentsRepository.create(feedId, message, sessionId))
      _ <- queueService.enqueueComment(id)
      _ <- listenerService.commentCreated(id, feedId, message, sessionId)
    } yield (id)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(commentsRepository.delete(commentId, sessionId))
      _ <- listenerService.commentDeleted(commentId, sessionId)
    } yield (())
  }

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {
    commentsRepository.find(feedId, since, offset, count, sessionId)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsRepository.find(
      commentId,
      sessionId
    )
  }

  def report(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(reportsRepository.createCommentReport(commentId, reportType, reportContent, sessionId))
      _ <- listenerService.commentReported(commentId, reportType, reportContent, sessionId)
    } yield (())
  }

}



