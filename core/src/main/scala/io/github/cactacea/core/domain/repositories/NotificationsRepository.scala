package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.domain.models.Notification
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class NotificationsRepository {

  @Inject private var notificationsDAO: NotificationsDAO = _
  @Inject private var friendRequestsDAO: FriendRequestsDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var feedsDAO: FeedsDAO = _
  @Inject private var commentsDAO: CommentsDAO = _

  def createFeed(id: FeedId, accountIds: List[AccountId]): Future[Unit] = {
    feedsDAO.find(id).flatMap(_ match {
      case Some(f) =>
        notificationsDAO.create(accountIds, NotificationType.friendRequest, id.value)
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
            notificationsDAO.create(accountId, notificationType, id.value)
              .flatMap(_ => Future.Unit)
          case None =>
            Future.Unit
        })
      case _ =>
        Future.Unit
    })
  }

  def createFriendRequest(id: FriendRequestId): Future[Unit] = {
    friendRequestsDAO.find(id).flatMap(_ match {
      case Some(f) =>
        notificationsDAO.create(f.accountId, NotificationType.friendRequest, id.value).flatMap(_ => Future.Unit)
      case None =>
        Future.Unit
    })
  }

  def createGroupInvitation(id: GroupInvitationId): Future[Unit] = {
    groupInvitationsDAO.find(id).flatMap(_ match {
      case Some(i) =>
        notificationsDAO.create(i.accountId, NotificationType.groupInvitation, id.value).flatMap(_ => Future.Unit)
      case None =>
        Future.Unit
    })
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Notification]] = {
    notificationsDAO.findAll(since, offset, count, sessionId).map(_.map(Notification(_)))
  }

  def updateReadStatus(notifications: List[Notification], sessionId: SessionId): Future[Unit] = {
    if (notifications.size == 0) {
      Future.Unit
    } else {
      notificationsDAO.updateUnread(notifications.map(_.id), sessionId).flatMap(_ => Future.Unit)
    }
  }

}
