package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
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
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val message = s"${displayName} : フィードを投稿しました。"
              val urlIos = s"cactacea://feeds/${f.id}"
              val urlAndroid = s"cactacea://feeds/${f.id}"
              PushNotification(message, urlIos, urlAndroid, f.postedAt, tokens, f.by.toSessionId)
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
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val message = s"${displayName} さんから招待されました。"
              val urlIos = s"cactacea://invites/${i.id}"
              val urlAndroid = s"cactacea://invites/${i.id}"
              PushNotification(message, urlIos, urlAndroid, i.invitedAt, tokens, i.by.toSessionId)
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
          t.groupBy(_.displayName).map({
            case (displayName, fanOuts) =>
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val message = s"${displayName} さんからメッセージが到着しました。"
              val urlIos = s"cactacea://groups/${m.groupId}/messages/${m.id}"
              val urlAndroid = s"cactacea://groups/${m.groupId}/messages/${m.id}"
              PushNotification(message, urlIos, urlAndroid, m.postedAt, tokens, m.by.toSessionId)
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
              val tokens = fanOuts.map(fanOut => (fanOut.accountId, fanOut.token))
              val message = s"${displayName} さんからコメントがきました。"
              val urlIos = s"cactacea://invites/${c.id}"
              val urlAndroid = s"cactacea://feeds/${c.id}"
              PushNotification(message, urlIos, urlAndroid, c.postedAt, tokens, c.by.toSessionId)
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
