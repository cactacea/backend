package io.github.cactacea.core.application.components.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{PublishService, SubScribeService}
import io.github.cactacea.core.infrastructure.identifiers._

@Singleton
class DefaultPublishService @Inject()(subScribeService: SubScribeService) extends PublishService {

  def enqueueFeed(feedId: FeedId): Future[Unit] = {
    subScribeService.dequeueFeed(feedId)
  }

  def enqueueComment(commentId: CommentId): Future[Unit] = {
    subScribeService.dequeueComment(commentId)
  }

  def enqueueMessage(messageId: MessageId): Future[Unit] = {
    subScribeService.dequeueMessage(messageId)
  }

  def enqueueGroupInvite(groupInvitationId: GroupInvitationId): Future[Unit] = {
    subScribeService.dequeueGroupInvite(groupInvitationId)
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    subScribeService.dequeueFriendRequest(friendRequestId)
  }

}
