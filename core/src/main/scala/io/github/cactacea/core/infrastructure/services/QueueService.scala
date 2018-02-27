package io.github.cactacea.core.infrastructure.services

import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._

trait QueueService {
  def enqueueDeliverMessage(messageId: MessageId): Future[Unit]
  def enqueueDeliverFeed(feedId: FeedId): Future[Unit]
  def enqueueNoticeMessage(messageId: MessageId): Future[Unit]
  def enqueueNoticeFeed(feedId: FeedId): Future[Unit]
  def enqueueNoticeFeedFavorite(feedId: FeedId): Future[Unit]
  def enqueueNoticeComment(commentId: CommentId): Future[Unit]
  def enqueueNoticeGroupInvite(groupInviteId: GroupInviteId): Future[Unit]
}
