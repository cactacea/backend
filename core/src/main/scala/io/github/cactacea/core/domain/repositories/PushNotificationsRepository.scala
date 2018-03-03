package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.PushNotificationType
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class PushNotificationsRepository {

  @Inject var pushNotificationsDAO: PushNotificationsDAO = _

  @Inject var feedsDAO: FeedsDAO = _
  @Inject var accountFeedsDAO: AccountFeedsDAO = _

  def findFeeds(feedId: FeedId) : Future[List[PushNotification]] = {
    feedsDAO.find(feedId).flatMap(_ match {
      case Some(f) if f.notified == false =>
        pushNotificationsDAO.findFeeds(feedId).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.postFeed
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

  def updateFeeds(feedId: FeedId, accountIds: List[AccountId]) : Future[Boolean] = {
    accountFeedsDAO.update(feedId, accountIds)
  }

  @Inject var groupInvitesDAO: GroupInvitesDAO = _

  def findGroupInvites(groupInviteId: GroupInviteId) : Future[List[PushNotification]] = {
    groupInvitesDAO.find(groupInviteId).flatMap(_ match {
      case Some(i) if i.notified == false =>
        pushNotificationsDAO.findGroupInvites(groupInviteId).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.sendGroupInvitation
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

  def updateGroupInvites(groupInviteId: GroupInviteId): Future[Boolean] = {
    groupInvitesDAO.updateNotified(groupInviteId)
  }


  @Inject var messagesDAO: MessagesDAO = _
  @Inject var accountMessagesDAO: AccountMessagesDAO = _

  def findMessages(messageId: MessageId) : Future[List[PushNotification]] = {
    messagesDAO.find(messageId).flatMap(_ match {
      case Some(m) if m.notified == false =>
        pushNotificationsDAO.findMessages(messageId).map({ t =>
          t.groupBy({ g => (g.displayName, g.showContent)}).map({
            case ((displayName, showContent), fanOuts) =>
              val pushType = showContent match {
                case true =>
                  PushNotificationType.sendMessage
                case false =>
                  PushNotificationType.sendNoDisplayedMessage
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

  def updateMessages(messageId: MessageId, accountIds: List[AccountId]): Future[Boolean] = {
    accountMessagesDAO.updateNotified(messageId, accountIds)
  }

  @Inject var commentsDAO: CommentsDAO = _

  def findComments(commentId: CommentId) : Future[List[PushNotification]] = {
    commentsDAO.find(commentId).flatMap(_ match {
      case Some(c) if c.notified == false =>
        pushNotificationsDAO.findComments(commentId).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val pushType = PushNotificationType.postComment
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

  def updateComments(commentId: CommentId): Future[Boolean] = {
    commentsDAO.updateNotified(commentId)
  }

}
