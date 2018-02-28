package io.github.cactacea.core.mocks

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.QueueService

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
