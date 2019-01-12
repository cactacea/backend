package io.github.cactacea.backend.core.application.components.services

import java.util.concurrent.Executors

import com.google.inject.Inject
import com.twitter.concurrent.NamedPoolThreadFactory
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.backend.core.application.components.interfaces.{FanOutService, QueueService}
import io.github.cactacea.backend.core.infrastructure.identifiers._

// No queue service

class DefaultQueueService @Inject()(fanOutService: FanOutService) extends QueueService {

  val fixedThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
    new NamedPoolThreadFactory("QueueFuturePool", makeDaemons = true)
  )
  val futurePool: FuturePool = FuturePool(fixedThreadExecutor)

  def enqueueFeed(feedId: FeedId): Future[Unit] = {
    futurePool {
      fanOutService.dequeueFeed(feedId)
    }
    Future.Unit
  }

  def enqueueComment(commentId: CommentId): Future[Unit] = {
    futurePool {
      fanOutService.dequeueComment(commentId)
    }
    Future.Unit
  }

  def enqueueMessage(messageId: MessageId): Future[Unit] = {
    futurePool {
      fanOutService.dequeueMessage(messageId)
    }
    Future.Unit
  }

  def enqueueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    futurePool {
      fanOutService.dequeueGroupInvitation(groupInvitationId)
    }
    Future.Unit
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    futurePool {
      fanOutService.dequeueFriendRequest(friendRequestId)
    }
    Future.Unit
  }

}
