package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.domain.models.{Comment, PushNotification}
import io.github.cactacea.backend.core.infrastructure.dao.{CommentsDAO, FeedsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class CommentsRepository @Inject()(
                                    commentsDAO: CommentsDAO,
                                    feedsDAO: FeedsDAO,
                                    deepLinkService: DeepLinkService
                                  ) {

  def find(feedId: FeedId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Comment]] = {
    for {
      _ <- feedsDAO.validateExist(feedId, sessionId)
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
      _ <- feedsDAO.validateExist(feedId, sessionId)
      r <- commentsDAO.create(feedId, message, sessionId)
    } yield (r)
  }

  def delete(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- commentsDAO.validateExist(commentId, sessionId)
      _ <- commentsDAO.delete(commentId, sessionId)
    } yield (Unit)
  }


  // Mobile Push

  def findPushNotifications(id: CommentId) : Future[List[PushNotification]] = {
    commentsDAO.find(id).flatMap(_ match {
      case Some(c) if c.notified == false => {
        val pushType = c.replyId.isDefined match {
          case true => PushNotificationType.commentReply
          case false => PushNotificationType.feedReply
        }
        val postedAt = c.postedAt
        val sessionId = c.by.toSessionId
        val url = deepLinkService.getComment(c.feedId, c.id)
        commentsDAO.findPushNotifications(id, c.replyId.isDefined).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case None =>
        Future.value(List[PushNotification]())
    })
  }

  def updatePushNotifications(id: CommentId): Future[Unit] = {
    commentsDAO.updatePushNotifications(id)
  }

}
