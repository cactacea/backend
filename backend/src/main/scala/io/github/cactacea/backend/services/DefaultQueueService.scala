package io.github.cactacea.backend.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.services.QueueService

@Singleton
class DefaultQueueService extends QueueService {

  @Inject var commentsService: CommentsService = _
  @Inject var messageService: MessagesService = _
  @Inject var feedsService: FeedsService = _
  @Inject var feedFavoritesService: FeedFavoritesService = _
  @Inject var groupInvitesService: GroupInvitesService = _

  override def enqueueDeliverMessage(messageId: MessageId): Future[Unit] = {
    messageService.deliver(messageId)
  }

  override def enqueueDeliverFeed(feedId: FeedId): Future[Unit] = {
    feedsService.deliver(feedId)
  }

  override def enqueueNoticeFeed(feedId: FeedId): Future[Unit] = {
    feedsService.notice(feedId)
  }

  override def enqueueNoticeFeedFavorite(feedId: FeedId): Future[Unit] = {
    feedFavoritesService.notice(feedId)
  }

  override def enqueueNoticeComment(commentId: CommentId): Future[Unit] = {
    commentsService.notice(commentId)
  }

  override def enqueueNoticeGroupInvite(groupInviteId: GroupInviteId): Future[Unit] = {
    groupInvitesService.notice(groupInviteId)
  }

  override def enqueueNoticeMessage(messageId: MessageId): Future[Unit] = {
    messageService.notice(messageId)
  }

}
