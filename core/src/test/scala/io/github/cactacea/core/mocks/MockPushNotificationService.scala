package io.github.cactacea.core.mocks

import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.PushNotificationService

class MockPushNotificationService extends PushNotificationService {

  override def notifyGroupInvite(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], groupId: GroupId, invitedAt: Long): Future[List[AccountId]] = {
    Future.value(tokens.map(_._2))
  }

  override def notifyComment(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], commentId: CommentId, postedAt: Long): Future[List[AccountId]] = {
    Future.value(tokens.map(_._2))
  }

  override def notifyMessage(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], messageId: MessageId, message: Option[String], mediumId: Option[MediumId], postedAt: Long, show: Boolean): Future[List[AccountId]] = {
    Future.value(tokens.map(_._2))
  }

  override def notifyFeed(accountId: AccountId, displayName: String, tokens: List[(String, AccountId)], feedId: FeedId, postedAt: Long): Future[List[AccountId]] = {
    Future.value(tokens.map(_._2))
  }

}
