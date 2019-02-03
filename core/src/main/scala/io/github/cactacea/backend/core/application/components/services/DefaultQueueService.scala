package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{MobilePushService, QueueService}
import io.github.cactacea.backend.core.infrastructure.identifiers._


class DefaultQueueService @Inject()(mobilePushService: MobilePushService) extends QueueService {

  import java.util.concurrent.LinkedBlockingQueue

  private val queue = new LinkedBlockingQueue[Queue]()
  private val receiver = new Receiver(queue, mobilePushService)

  override def start(): Unit = {
    new Thread(receiver).start()
  }

  def enqueueFeed(feedId: FeedId): Future[Unit] = {
    queue.put(FeedQueue(feedId))
    Future.Unit
  }

  def enqueueComment(commentId: CommentId): Future[Unit] = {
    queue.put(CommentQueue(commentId))
    Future.Unit
  }

  def enqueueMessage(messageId: MessageId): Future[Unit] = {
    queue.put(MessageQueue(messageId))
    Future.Unit
  }

  def enqueueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    queue.put(GroupInvitationQueue(groupInvitationId))
    Future.Unit
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    queue.put(FriendRequestQueue(friendRequestId))
    Future.Unit
  }

  import java.util.concurrent.BlockingQueue

  private class Receiver(val queue: BlockingQueue[Queue], mobilePushService: MobilePushService) extends Runnable {

    var finished = false

    override def run(): Unit = {
      while (!finished) {
        val item = queue.take()
        item match {
          case q: FeedQueue =>            mobilePushService.sendFeed(q.id)
          case q: CommentQueue =>         mobilePushService.sendComment(q.id)
          case q: MessageQueue =>         mobilePushService.sendMessage(q.id)
          case q: GroupInvitationQueue => mobilePushService.sendGroupInvitation(q.id)
          case q: FriendRequestQueue =>   mobilePushService.sendFriendRequest(q.id)

        }
      }
    }

  }

  private trait Queue
  private case class FeedQueue(id: FeedId) extends Queue
  private case class CommentQueue(id: CommentId) extends Queue
  private case class MessageQueue(id: MessageId) extends Queue
  private case class GroupInvitationQueue(id: GroupInvitationId) extends Queue
  private case class FriendRequestQueue(id: FriendRequestId) extends Queue


}


