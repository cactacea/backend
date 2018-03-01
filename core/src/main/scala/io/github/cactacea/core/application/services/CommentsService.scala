package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.PublishService
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.domain.models.Comment
import io.github.cactacea.core.domain.repositories.{CommentsRepository, ReportsRepository}
import io.github.cactacea.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class CommentsService @Inject()(db: DatabaseService) {

  @Inject var commentsRepository: CommentsRepository = _
  @Inject var reportsRepository: ReportsRepository = _
  @Inject var publishService: PublishService = _

  def create(feedId: FeedId, message: String, sessionId: SessionId): Future[CommentId] = {
    for {
      id <- db.transaction(commentsRepository.create(feedId, message, sessionId))
      _ <- publishService.enqueueComment(id)
    } yield (id)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      commentsRepository.delete(commentId, sessionId)
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

  def report(replyId: CommentId, reportType: ReportType, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      reportsRepository.createCommentReport(replyId, reportType, sessionId)
    }
  }

}

//  @Inject var queueService: OldQueueService = _
//  @Inject var deliveryCommentsRepository: DeliveryCommentsRepository = _
//  @Inject var pushNotificationService: OldPushNotificationService = _
//    for {
//      id <- db.transaction(commentsRepository.create(feedId, message, sessionId))
//      _ <- queueService.enqueueNoticeComment(id)
//    } yield (id)
//  def notice(commentId: CommentId): Future[Unit] = {
//    deliveryCommentsRepository.findAll(commentId).flatMap(_ match {
//      case Some(comments) =>
//        Future.traverseSequentially(comments) { c =>
//          db.transaction {
//            for {
//              accountIds <- pushNotificationService.notifyComment(c.accountId, c.displayName, c.tokens, c.commentId, c.postedAt)
//            } yield (accountIds.size == c.tokens.size)
//          }
//        }.flatMap(_.filter(_ == false).size match {
//          case 0L =>
//            db.transaction {
//              deliveryCommentsRepository.updateNotified(commentId).flatMap(_ => Future.Unit)
//            }
//          case _ =>
//            Future.exception(PushNotificationException())
//        })
//      case None =>
//        Future.Unit
//    })
//  }

