package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait NotificationService {
  def fanOutFeed(feedId: FeedId): Future[Unit]
  def fanOutComment(commentId: CommentId): Future[Unit]
  def fanOutMessage(messageId: MessageId): Future[Unit]
  def fanOutGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit]
  def fanOutFriendRequest(friendRequestId: FriendRequestId): Future[Unit]
}
