package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.{Future, FuturePool}
import io.github.cactacea.backend.core.application.components.interfaces.{MobilePushService, QueueService}
import io.github.cactacea.backend.core.infrastructure.identifiers._


class DefaultQueueService @Inject()(mobilePushService: MobilePushService) extends QueueService {

  override def start(): Unit = {
  }

  def enqueueTweet(tweetId: TweetId): Future[Unit] = {
    FuturePool.unboundedPool {
      mobilePushService.sendTweet(tweetId)
    }
  }

  def enqueueComment(commentId: CommentId): Future[Unit] = {
    FuturePool.unboundedPool {
      mobilePushService.sendComment(commentId)
    }
  }

  def enqueueMessage(messageId: MessageId): Future[Unit] = {
    FuturePool.unboundedPool {
      mobilePushService.sendMessage(messageId)
    }
  }

  def enqueueInvitation(invitationId: InvitationId): Future[Unit] = {
    FuturePool.unboundedPool {
      mobilePushService.sendInvitation(invitationId)
    }
  }

  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    FuturePool.unboundedPool {
      mobilePushService.sendFriendRequest(friendRequestId)
    }
  }

}


//import java.util.concurrent.LinkedBlockingQueue
//import com.google.inject.Inject
//import com.twitter.util.Future
//import io.github.cactacea.backend.core.application.components.interfaces.{MobilePushService, QueueService}
//import io.github.cactacea.backend.core.infrastructure.identifiers._
//
//
//class DefaultQueueService @Inject()(mobilePushService: MobilePushService) extends QueueService {
//
//  private val queue = new LinkedBlockingQueue[Queue]()
//  private val receiver = new Receiver(queue, mobilePushService)
//
//  override def start(): Unit = {
//    new Thread(receiver).start()
//  }
//
//  def enqueueTweet(tweetId: TweetId): Future[Unit] = {
//    queue.put(TweetQueue(tweetId))
//    Future.Unit
//  }
//
//  def enqueueComment(commentId: CommentId): Future[Unit] = {
//    queue.put(CommentQueue(commentId))
//    Future.Unit
//  }
//
//  def enqueueMessage(messageId: MessageId): Future[Unit] = {
//    queue.put(MessageQueue(messageId))
//    Future.Unit
//  }
//
//  def enqueueInvitation(invitationId: InvitationId): Future[Unit] = {
//    queue.put(InvitationQueue(invitationId))
//    Future.Unit
//  }
//
//  def enqueueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
//    queue.put(FriendRequestQueue(friendRequestId))
//    Future.Unit
//  }
//
//  import java.util.concurrent.BlockingQueue
//
//  private class Receiver(val queue: BlockingQueue[Queue], mobilePushService: MobilePushService) extends Runnable {
//
//    var finished = false
//
//    override def run(): Unit = {
//      while (!finished) {
//        val item = queue.take()
//        item match {
//          case q: TweetQueue =>            mobilePushService.sendTweet(q.id)
//          case q: CommentQueue =>         mobilePushService.sendComment(q.id)
//          case q: MessageQueue =>         mobilePushService.sendMessage(q.id)
//          case q: InvitationQueue => mobilePushService.sendInvitation(q.id)
//          case q: FriendRequestQueue =>   mobilePushService.sendFriendRequest(q.id)
//
//        }
//      }
//    }
//
//  }
//
//  private trait Queue
//  private case class TweetQueue(id: TweetId) extends Queue
//  private case class CommentQueue(id: CommentId) extends Queue
//  private case class MessageQueue(id: MessageId) extends Queue
//  private case class InvitationQueue(id: InvitationId) extends Queue
//  private case class FriendRequestQueue(id: FriendRequestId) extends Queue
//
//
//}


