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
                                 publishService: QueueService,
                                 actionService: ListenerService
                               ) {

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    db.transaction {
      for {
        id <- commentsRepository.create(feedId, message, sessionId)
        _ <- publishService.enqueueComment(id)
        _ <- actionService.commentCreated(id, feedId, message, sessionId)
      } yield (id)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- commentsRepository.delete(commentId, sessionId)
        _ <- actionService.commentDeleted(commentId, sessionId)
      } yield (Unit)
    }
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
      _ <- actionService.commentReported(commentId, reportType, reportContent, sessionId)
    } yield (Unit)
  }

}



