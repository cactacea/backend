package io.github.cactacea.core.infrastructure.clients.onesignal

import com.typesafe.config.ConfigFactory
import io.github.cactacea.core.infrastructure.identifiers._

object OneSignalNotificationFactory {

  private lazy val config = ConfigFactory.load().atPath("onesignal")
  private lazy val appId = config.getString("appId")

  def createGroupInvite(accountId: AccountId, displayName: String, tokens: List[String], groupId: GroupId, invitedAt: Long) = {
    val content = OneSignalNotificationContentFactory.createGroupInvite(displayName)
    OneSignalNotification(appId, content, tokens, accountId, displayName, Some(groupId), Some(invitedAt), None, None, None, None)
  }

  def createMessage(accountId: AccountId, displayName: String, tokens: List[String], messageId: MessageId, message: Option[String], mediumId: Option[MediumId], postedAt: Long, show: Boolean) = {
    val content = OneSignalNotificationContentFactory.createMessage(displayName, message, mediumId, show)
    OneSignalNotification(appId, content, tokens, accountId, displayName, None, None, None, None, Some(messageId), Some(postedAt))
  }

  def createFeed(accountId: AccountId, displayName: String, tokens: List[String], feedId: FeedId, postedAt: Long) = {
    val content = OneSignalNotificationContentFactory.createFeed(displayName)
    OneSignalNotification(appId, content, tokens, accountId, displayName, None, None, Some(feedId), None, None, Some(postedAt))
  }

  def createComment(accountId: AccountId, displayName: String, tokens: List[String], commentId: CommentId, postedAt: Long) = {
    val content = OneSignalNotificationContentFactory.createComment()
    OneSignalNotification(appId, content, tokens, accountId, displayName, None, None, None, Some(commentId), None, Some(postedAt))
  }

}
