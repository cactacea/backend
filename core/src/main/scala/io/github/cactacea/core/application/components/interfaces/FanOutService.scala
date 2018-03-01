package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._

trait FanOutService {
  def fanOutFeed(feedId: FeedId): Future[Unit]
  def fanOutComment(commentId: CommentId): Future[Unit]
  def fanOutMessage(messageId: MessageId): Future[Unit]
  def fanOutGroupInvite(groupInviteId: GroupInviteId): Future[Unit]
  def fanOutFriendRequest(friendRequestId: FriendRequestId): Future[Unit]
  //  def fanOutGroup(groupId: GroupId): Future[Unit]
  //  def fanOutFavoriteFeed(feedId: FeedId): Future[Unit]
}
