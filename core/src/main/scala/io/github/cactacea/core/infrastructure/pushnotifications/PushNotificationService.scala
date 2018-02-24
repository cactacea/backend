package io.github.cactacea.core.infrastructure.pushnotifications

import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._

trait PushNotificationService {
  def notifyGroupInvite(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], groupId: GroupId, invitedAt: Long): Future[List[AccountId]]
  def notifyComment(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], commentId: CommentId, postedAt: Long): Future[List[AccountId]]
  def notifyMessage(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], messageId: MessageId, message: Option[String], mediumId: Option[MediumId], postedAt: Long, show: Boolean): Future[List[AccountId]]
  def notifyFeed(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], feedId: FeedId, postedAt: Long): Future[List[AccountId]]
}
