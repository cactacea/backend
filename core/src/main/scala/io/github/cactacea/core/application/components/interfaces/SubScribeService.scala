package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._

trait SubScribeService {
  def dequeueFeed(feedId: FeedId): Future[Unit]
  def dequeueComment(commentId: CommentId): Future[Unit]
  def dequeueMessage(messageId: MessageId): Future[Unit]
  def dequeueGroupInvite(groupInviteId: GroupInviteId): Future[Unit]
  def dequeueFriendRequest(friendRequestId: FriendRequestId): Future[Unit]
}
