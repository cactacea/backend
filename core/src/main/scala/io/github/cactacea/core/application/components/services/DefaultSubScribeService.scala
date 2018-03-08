package io.github.cactacea.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{FanOutService, SubScribeService}
import io.github.cactacea.core.infrastructure.identifiers._

class DefaultSubScribeService extends SubScribeService {

  @Inject private var fanOutService: FanOutService = _

  def dequeueFeed(feedId: FeedId): Future[Unit] = {
    fanOutService.fanOutFeed(feedId)
  }

  def dequeueComment(commentId: CommentId): Future[Unit] = {
    fanOutService.fanOutComment(commentId)
  }

  def dequeueMessage(messageId: MessageId): Future[Unit] = {
    fanOutService.fanOutMessage(messageId)
  }

  def dequeueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    fanOutService.fanOutGroupInvitation(groupInvitationId)
  }

  def dequeueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    fanOutService.fanOutFriendRequest(friendRequestId)
  }

}
