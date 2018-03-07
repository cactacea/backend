package io.github.cactacea.core.application.components.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{FanOutService, SubScribeService}
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class DefaultSubScribeService @Inject()(fanOutService: FanOutService) extends SubScribeService {

  def dequeueFeed(feedId: FeedId): Future[Unit] = {
    fanOutService.fanOutFeed(feedId)
  }

  def dequeueComment(commentId: CommentId): Future[Unit] = {
    fanOutService.fanOutComment(commentId)
  }

  def dequeueMessage(messageId: MessageId): Future[Unit] = {
    fanOutService.fanOutMessage(messageId)
  }

  def dequeueGroupInvite(groupInvitationId: GroupInvitationId): Future[Unit] = {
    fanOutService.fanOutGroupInvite(groupInvitationId)
  }

  def dequeueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    fanOutService.fanOutFriendRequest(friendRequestId)
  }

}
