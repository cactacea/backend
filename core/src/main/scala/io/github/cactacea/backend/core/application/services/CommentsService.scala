package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, PublishService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.domain.repositories.{CommentsRepository, ReportsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}

@Singleton
class CommentsService {

  @Inject private var db: DatabaseService = _
  @Inject private var commentsRepository: CommentsRepository = _
  @Inject private var reportsRepository: ReportsRepository = _
  @Inject private var publishService: PublishService = _
  @Inject private var actionService: InjectionService = _

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    db.transaction {
      for {
        id <- commentsRepository.create(feedId, message, sessionId)
        _ <- publishService.enqueueComment(id)
        _ <- actionService.commentCreated(id, sessionId)
      } yield (id)
    }
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        r <- commentsRepository.delete(commentId, sessionId)
        _ <- actionService.commentDeleted(commentId, sessionId)
      } yield (r)
    }
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



