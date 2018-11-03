package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{EnqueueService, DequeueService}
import io.github.cactacea.backend.core.infrastructure.identifiers._

// No queue service

class DefaultEnqueueService @Inject()(dequeue: DequeueService) extends EnqueueService {

  def enqueueFeed(feedId: FeedId): Future[Unit] = {
    dequeue.dequeueFeed(feedId)
  }

  def enqueueComment(commentId: CommentId): Future[Unit] = {
    dequeue.dequeueComment(commentId)
  }

  def enqueueMessage(messageId: MessageId): Future[Unit] = {
    dequeue.dequeueMessage(messageId)
  }

  def enqueueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    dequeue.dequeueGroupInvitation(groupInvitationId)
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    dequeue.dequeueFriendRequest(friendRequestId)
  }

}
