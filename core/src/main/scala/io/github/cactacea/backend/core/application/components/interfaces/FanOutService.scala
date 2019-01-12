package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait FanOutService {
  def dequeueFeed(feedId: FeedId): Future[Unit]
  def dequeueComment(commentId: CommentId): Future[Unit]
  def dequeueMessage(messageId: MessageId): Future[Unit]
  def dequeueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit]
  def dequeueFriendRequest(friendRequestId: FriendRequestId): Future[Unit]
}
