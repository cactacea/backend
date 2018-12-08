package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.backend.core.application.components.interfaces.{DequeueService, EnqueueService}
import io.github.cactacea.backend.core.infrastructure.identifiers._

// No queue service

class DefaultEnqueueService @Inject()(dequeue: DequeueService) extends EnqueueService {

  def enqueueFeed(feedId: FeedId): Future[Unit] = {
    FuturePool.interruptibleUnboundedPool {
      dequeue.dequeueFeed(feedId)
    }
    Future.Unit
  }

  def enqueueComment(commentId: CommentId): Future[Unit] = {
    FuturePool.interruptibleUnboundedPool {
      dequeue.dequeueComment(commentId)
    }
    Future.Unit
  }

  def enqueueMessage(messageId: MessageId): Future[Unit] = {
    FuturePool.interruptibleUnboundedPool {
      dequeue.dequeueMessage(messageId)
    }
    Future.Unit
  }

  def enqueueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    FuturePool.interruptibleUnboundedPool {
      dequeue.dequeueGroupInvitation(groupInvitationId)
    }
    Future.Unit
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    FuturePool.interruptibleUnboundedPool {
      dequeue.dequeueFriendRequest(friendRequestId)
    }
    Future.Unit
  }

}
