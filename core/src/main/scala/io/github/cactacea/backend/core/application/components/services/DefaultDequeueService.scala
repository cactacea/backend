package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{NotificationService, DequeueService}
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultDequeueService extends DequeueService {

  @Inject private var notificationService: NotificationService = _

  def dequeueFeed(feedId: FeedId): Future[Unit] = {
    notificationService.fanOutFeed(feedId)
  }

  def dequeueComment(commentId: CommentId): Future[Unit] = {
    notificationService.fanOutComment(commentId)
  }

  def dequeueMessage(messageId: MessageId): Future[Unit] = {
    notificationService.fanOutMessage(messageId)
  }

  def dequeueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    notificationService.fanOutGroupInvitation(groupInvitationId)
  }

  def dequeueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    notificationService.fanOutFriendRequest(friendRequestId)
  }

}
