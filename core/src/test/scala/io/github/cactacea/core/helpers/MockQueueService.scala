package io.github.cactacea.core.helpers

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.application.interfaces.QueueService
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class MockQueueService extends QueueService {

  override def enqueueDeliverMessage(messageId: MessageId): Future[Unit] = {
    Future.Unit
  }

  override def enqueueDeliverFeed(feedId: FeedId): Future[Unit] = {
    Future.Unit
  }

  override def enqueueNoticeFeed(feedId: FeedId): Future[Unit] = {
    Future.Unit
  }

  override def enqueueNoticeFeedFavorite(feedId: FeedId): Future[Unit] = {
    Future.Unit
  }

  override def enqueueNoticeComment(commentId: CommentId): Future[Unit] = {
    Future.Unit
  }

  override def enqueueNoticeGroupInvite(groupInviteId: GroupInviteId): Future[Unit] = {
    Future.Unit
  }

  override def enqueueNoticeMessage(messageId: MessageId): Future[Unit] = {
    Future.Unit
  }

}
