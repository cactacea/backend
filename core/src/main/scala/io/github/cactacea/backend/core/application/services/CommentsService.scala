package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.QueueService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.domain.repositories.CommentsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}

class CommentsService @Inject()(
                                 commentsRepository: CommentsRepository,
                                 databaseService: DatabaseService,
                                 queueService: QueueService
                               ) {

  import databaseService._

  def create(feedId: FeedId, message: String, replyId: Option[CommentId], sessionId: SessionId): Future[CommentId] = {
    transaction {
      for {
        i <- commentsRepository.create(feedId, message, replyId, sessionId)
        _ <- queueService.enqueueComment(i)
      } yield (i)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    transaction{
      commentsRepository.delete(commentId, sessionId)
    }
  }

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {
    commentsRepository.find(feedId, since, offset, count, sessionId)
  }

  def find(commentId: CommentId, sessionId: SessionId): Future[Comment] = {
    commentsRepository.find(commentId, sessionId)
  }

  def report(commentId: CommentId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      commentsRepository.report(commentId, reportType, reportContent, sessionId)
    }
  }

}



