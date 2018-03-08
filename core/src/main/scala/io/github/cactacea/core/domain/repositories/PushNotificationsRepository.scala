package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.PushNotificationType
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class PushNotificationsRepository {

  @Inject private var pushNotificationsDAO: PushNotificationsDAO = _
  @Inject private var feedsDAO: FeedsDAO = _
  @Inject private var accountFeedsDAO: AccountFeedsDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var messagesDAO: MessagesDAO = _
  @Inject private var accountMessagesDAO: AccountMessagesDAO = _
  @Inject private var commentsDAO: CommentsDAO = _
  @Inject private var friendRequestsDAO: FriendRequestsDAO = _

  def findByFeedId(id: FeedId) : Future[List[PushNotification]] = {
    feedsDAO.find(id).flatMap(_ match {
      case Some(f) if f.notified == false =>
        pushNotificationsDAO.findByFeed(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.feed
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val postedAt = f.postedAt
              val sessionId = f.by.toSessionId
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, None, None, None)
          }).toList
        })
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByInvitationId(id: GroupInvitationId) : Future[List[PushNotification]] = {
    groupInvitationsDAO.find(id).flatMap(_ match {
      case Some(i) if i.notified == false =>
        pushNotificationsDAO.findByInvitationId(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.groupInvitation
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val postedAt = i.invitedAt
              val sessionId = i.by.toSessionId
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, None, None, None)
          }).toList
        })
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByFriendRequestId(id: FriendRequestId) : Future[List[PushNotification]] = {
    friendRequestsDAO.find(id).flatMap(_ match {
      case Some(i) if i.notified == false =>
        pushNotificationsDAO.findByFriendRequestId(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.groupInvitation
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val postedAt = i.requestedAt
              val sessionId = i.by.toSessionId
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, None, None, None)
          }).toList
        })
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByMessageId(id: MessageId) : Future[List[PushNotification]] = {
    messagesDAO.find(id).flatMap(_ match {
      case Some(m) if m.notified == false =>
        pushNotificationsDAO.findByMessageId(id).map({ t =>
          t.groupBy({ g => (g.displayName, g.showContent)}).map({
            case ((displayName, showContent), fanOuts) =>
              val pushType = showContent match {
                case true =>
                  PushNotificationType.message
                case false =>
                  PushNotificationType.noDisplayedMessage
              }
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val postedAt = m.postedAt
              val sessionId = m.by.toSessionId
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, None, None, None)
          }).toList
        })
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByCommentId(id: CommentId) : Future[List[PushNotification]] = {
    commentsDAO.find(id).flatMap(_ match {
      case Some(c) if c.notified == false =>
        pushNotificationsDAO.findByCommentId(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.comment
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val postedAt = c.postedAt
              val sessionId = c.by.toSessionId
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, None, None, None)
          }).toList
        })
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def updateFeedNotified(id: FeedId, accountIds: List[AccountId]) : Future[Boolean] = {
    accountFeedsDAO.update(id, accountIds)
  }

  def updateInvitationNotified(id: GroupInvitationId): Future[Boolean] = {
    groupInvitationsDAO.updateNotified(id)
  }

  def updateFriendRequestNotified(id: FriendRequestId): Future[Boolean] = {
    friendRequestsDAO.updateNotified(id)
  }

  def updateMessageNotified(id: MessageId, accountIds: List[AccountId]): Future[Boolean] = {
    accountMessagesDAO.updateNotified(id, accountIds)
  }

  def updateCommentNotified(id: CommentId): Future[Boolean] = {
    commentsDAO.updateNotified(id)
  }

}
