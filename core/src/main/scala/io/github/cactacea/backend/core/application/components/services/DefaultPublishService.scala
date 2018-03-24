package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{PublishService, SubScribeService}
import io.github.cactacea.backend.core.infrastructure.identifiers._

// No queue service

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

  def enqueueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    subScribeService.dequeueGroupInvitation(groupInvitationId)
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    subScribeService.dequeueFriendRequest(friendRequestId)
  }

}
