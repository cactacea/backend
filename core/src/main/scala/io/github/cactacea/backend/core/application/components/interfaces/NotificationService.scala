package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait NotificationService {
  def notifyNewFeedArrived(feedId: FeedId): Future[Unit]
  def notifyNewCommentArrived(commentId: CommentId): Future[Unit]
  def notifyNewMessageArrived(messageId: MessageId): Future[Unit]
  def notifyNewGroupInvitationArrived(groupInvitationId: GroupInvitationId): Future[Unit]
  def notifyNewFriendRequestArrived(friendRequestId: FriendRequestId): Future[Unit]
}
