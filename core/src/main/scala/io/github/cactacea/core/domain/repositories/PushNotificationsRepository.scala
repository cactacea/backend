package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.core.domain.enums.PushNotificationType
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers._

// TODO : Refactoring

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
  @Inject private var deepLinkService: DeepLinkService = _

  def findByFeedId(id: FeedId) : Future[List[PushNotification]] = {
    feedsDAO.find(id).flatMap(_ match {
      case Some(f) if f.notified == false => {
        val pushType = PushNotificationType.feed
        val postedAt = f.postedAt
        val sessionId = f.by.toSessionId
        val url = deepLinkService.getFeed(id)
        pushNotificationsDAO.findByFeed(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByInvitationId(id: GroupInvitationId) : Future[List[PushNotification]] = {
    groupInvitationsDAO.find(id).flatMap(_ match {
      case Some(i) if i.notified == false => {
        val pushType = PushNotificationType.groupInvitation
        val postedAt = i.invitedAt
        val sessionId = i.by.toSessionId
        val url = deepLinkService.getInvitation(id)
        pushNotificationsDAO.findByInvitationId(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByRequestId(id: FriendRequestId) : Future[List[PushNotification]] = {
    friendRequestsDAO.find(id).flatMap(_ match {
      case Some(i) if i.notified == false => {
        val pushType = PushNotificationType.groupInvitation
        val postedAt = i.requestedAt
        val sessionId = i.by.toSessionId
        val url = deepLinkService.getRequest(id)
        pushNotificationsDAO.findByFriendRequestId(id).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByMessageId(id: MessageId) : Future[List[PushNotification]] = {
    messagesDAO.find(id).flatMap(_ match {
      case Some(m) if m.notified == false => {
        val postedAt = m.postedAt
        val sessionId = m.by.toSessionId
        val url = deepLinkService.getMessages(m.groupId, m.id)
        pushNotificationsDAO.findByMessageId(id).map({ t =>
          t.groupBy({ g => (g.displayName, g.showContent)}).map({
            case ((displayName, showContent), fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val pushType = showContent match {
                case true =>
                  PushNotificationType.message
                case false =>
                  PushNotificationType.noDisplayedMessage
              }
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
      case _ =>
        Future.value(List[PushNotification]())
    })
  }

  def findByCommentId(id: CommentId) : Future[List[PushNotification]] = {
    commentsDAO.find(id).flatMap(_ match {
      case Some(c) if c.notified == false => {
        val pushType = c.replyId.isDefined match {
          case true => PushNotificationType.commentReply
          case false => PushNotificationType.feedReply
        }
        val postedAt = c.postedAt
        val sessionId = c.by.toSessionId
        val url = deepLinkService.getComment(c.feedId, c.id)
        pushNotificationsDAO.findByCommentId(id, c.replyId.isDefined).map({ t =>
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              PushNotification(displayName, pushType, postedAt, tokens, sessionId, url)
          }).toList
        })
      }
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
