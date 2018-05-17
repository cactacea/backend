package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait PublishService {
  def enqueueFeed(feedId: FeedId): Future[Unit]
  def enqueueComment(commentId: CommentId): Future[Unit]
  def enqueueMessage(messageId: MessageId): Future[Unit]
  def enqueueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit]
  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit]
}