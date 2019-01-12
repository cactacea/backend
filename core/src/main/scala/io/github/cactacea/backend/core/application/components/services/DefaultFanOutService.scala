package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{ChatService, FanOutService, MobilePushService}
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.dao.MessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultFanOutService @Inject()(db: DatabaseService,
                                     messagesDAO: MessagesDAO,
                                     finatraObjectMapper: FinatraObjectMapper,
                                     mobilePushService: MobilePushService,
                                     feedsRepository: FeedsRepository,
                                     groupInvitationsRepository: GroupInvitationsRepository,
                                     friendRequestsRepository: FriendRequestsRepository,
                                     messagesRepository: MessagesRepository,
                                     commentsRepository: CommentsRepository,

                                     notificationsRepository: NotificationsRepository,
                                     chatService: ChatService) extends FanOutService {

  def dequeueFeed(feedId: FeedId): Future[Unit] = {
    db.transaction{
      for {
        p <- feedsRepository.findPushNotifications(feedId)
        a <- mobilePushService.send(p)
        _ <- feedsRepository.updatePushNotifications(feedId, a)
        _ <- notificationsRepository.createFeed(feedId, a)
      } yield (Unit)
    }
  }

  def dequeueComment(commentId: CommentId): Future[Unit] = {
    db.transaction{
      for {
        p <- commentsRepository.findPushNotifications(commentId)
        _ <- mobilePushService.send(p)
        _ <- commentsRepository.updatePushNotifications(commentId)
        _ <- notificationsRepository.createComment(commentId)
      } yield (Unit)
    }
  }

  def dequeueMessage(messageId: MessageId): Future[Unit] = {
    db.transaction{
      for {
        p <- messagesRepository.findPushNotifications(messageId)
        a <- mobilePushService.send(p)
        _ <- messagesRepository.updatePushNotifications(messageId, a)
        _ <- messagesDAO.find(messageId).map(_ match {
          case Some(m) =>
            chatService.publish(m.groupId, finatraObjectMapper.writeValueAsString(m))
          case None =>
            Future.Done
        })
      } yield (Unit)
    }
  }

  def dequeueGroupInvitation(groupInvitationId: GroupInvitationId): Future[Unit] = {
    for {
      p <- groupInvitationsRepository.findPushNotifications(groupInvitationId)
      _ <- mobilePushService.send(p)
      _ <- groupInvitationsRepository.updatePushNotifications(groupInvitationId)
      _ <- notificationsRepository.createInvitation(groupInvitationId)
    } yield (Unit)
  }

  def dequeueFriendRequest(friendRequestId: FriendRequestId): Future[Unit] = {
    for {
      p <- friendRequestsRepository.findPushNotifications(friendRequestId)
      _ <- mobilePushService.send(p)
      _ <- friendRequestsRepository.updatePushNotifications(friendRequestId)
      _ <- notificationsRepository.createRequest(friendRequestId)
    } yield (Unit)
  }

}
