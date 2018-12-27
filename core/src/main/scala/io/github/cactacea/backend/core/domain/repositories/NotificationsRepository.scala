package io.github.cactacea.backend.core.domain.repositories

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class NotificationsRepository @Inject()(
                                         commentsDAO: CommentsDAO,
                                         deepLinkService: DeepLinkService,
                                         feedsDAO: FeedsDAO,
                                         friendRequestsDAO: FriendRequestsDAO,
                                         groupInvitationsDAO: GroupInvitationsDAO,
                                         notificationsDAO: NotificationsDAO
                                       ) {

  def createFeed(id: FeedId, accountIds: List[AccountId]): Future[Unit] = {
    val url = deepLinkService.getFeed(id)
    feedsDAO.find(id).flatMap(_ match {
      case Some(f) =>
        notificationsDAO.create(accountIds, f.by, NotificationType.feed, id.value, url)
          .flatMap(_ => Future.Unit)
      case None =>
        Future.Unit
    })
  }

  def createComment(id: CommentId): Future[Unit] = {
    commentsDAO.find(id).flatMap(_ match {
      case Some(c) =>
        (c.replyId match {
          case Some(replyId) => commentsDAO.find(replyId).map(_.map(c => (c.by, NotificationType.commentReply)))
          case _ => Future.None
        }).flatMap(_ match {
          case None =>
            feedsDAO.find(c.feedId).map(_.map(f => (f.by, NotificationType.feedReply)))
          case a =>
            Future.value(a)
        }).flatMap(_ match {
          case Some((accountId, notificationType)) =>
            val url = deepLinkService.getComment(c.feedId, c.id)
            notificationsDAO.create(accountId, c.by, notificationType, id.value, url).flatMap(_ => Future.Unit)
          case None =>
            Future.Unit
        })
      case _ =>
        Future.Unit
    })
  }

  def createRequest(id: FriendRequestId): Future[Unit] = {
    val url = deepLinkService.getRequest(id)
    friendRequestsDAO.find(id).flatMap(_ match {
      case Some(f) =>
        notificationsDAO.create(f.accountId, f.by, NotificationType.friendRequest, id.value, url).flatMap(_ => Future.Unit)
      case None =>
        Future.Unit
    })
  }

  def createInvitation(id: GroupInvitationId): Future[Unit] = {
    val url = deepLinkService.getInvitation(id)
    groupInvitationsDAO.find(id).flatMap(_ match {
      case Some(i) =>
        notificationsDAO.create(i.accountId, i.by, NotificationType.groupInvitation, id.value, url).flatMap(_ => Future.Unit)
      case None =>
        Future.Unit
    })
  }

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[List[Notification]] = {
    notificationsDAO.find(since, offset, count, locales, sessionId)
  }

  def updateReadStatus(notifications: List[Notification], sessionId: SessionId): Future[Unit] = {
    if (notifications.size == 0) {
      Future.Unit
    } else {
      notificationsDAO.updateUnread(notifications.map(_.id), sessionId).flatMap(_ => Future.Unit)
    }
  }

}
