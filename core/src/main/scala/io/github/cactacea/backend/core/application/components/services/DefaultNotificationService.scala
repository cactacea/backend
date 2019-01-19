package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{ChatService, MobilePushService, NotificationService}
import io.github.cactacea.backend.core.domain.repositories._
import io.github.cactacea.backend.core.infrastructure.dao.MessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultNotificationService @Inject()(
                                           chatService: ChatService,
                                           commentsRepository: CommentsRepository,
                                           db: DatabaseService,
                                           feedsRepository: FeedsRepository,
                                           friendRequestsRepository: FriendRequestsRepository,
                                           groupInvitationsRepository: GroupInvitationsRepository,
                                           messagesDAO: MessagesDAO,
                                           mobilePushService: MobilePushService,
                                           messagesRepository: MessagesRepository,
                                           notificationsRepository: NotificationsRepository
                                          ) extends NotificationService {

  def notifyNewFeedArrived(feedId: FeedId): Future[Unit] = {
    db.transaction{
      for {
        p <- feedsRepository.findPushNotifications(feedId)
        a <- mobilePushService.send(p)
        _ <- feedsRepository.updatePushNotifications(feedId, a)
        _ <- notificationsRepository.createFeed(feedId, a)
      } yield (Unit)
    }
  }

  def notifyNewCommentArrived(commentId: CommentId): Future[Unit] = {
    db.transaction{
      for {
        p <- commentsRepository.findPushNotifications(commentId)
        _ <- mobilePushService.send(p)
        _ <- commentsRepository.updatePushNotifications(commentId)
        _ <- notificationsRepository.createComment(commentId)
      } yield (Unit)
    }
  }

  def notifyNewMessageArrived(messageId: MessageId): Future[Unit] = {
    db.transaction{
      for {
        p <- messagesRepository.findPushNotifications(messageId)
        a <- mobilePushService.send(p)
        _ <- messagesRepository.updatePushNotifications(messageId, a)
        _ <- messagesDAO.find(messageId).map(_ match {
          case Some(m) =>
            chatService.publish(m.groupId, m)
          case None =>
            Future.Done
        })
      } yield (Unit)
    }
  }

  def notifyNewGroupInvitationArrived(groupInvitationId: GroupInvitationId): Future[Unit] = {
    for {
      p <- groupInvitationsRepository.findPushNotifications(groupInvitationId)
      _ <- mobilePushService.send(p)
      _ <- groupInvitationsRepository.updatePushNotifications(groupInvitationId)
      _ <- notificationsRepository.createInvitation(groupInvitationId)
    } yield (Unit)
  }

  def notifyNewFriendRequestArrived(friendRequestId: FriendRequestId): Future[Unit] = {
    for {
      p <- friendRequestsRepository.findPushNotifications(friendRequestId)
      _ <- mobilePushService.send(p)
      _ <- friendRequestsRepository.updatePushNotifications(friendRequestId)
      _ <- notificationsRepository.createRequest(friendRequestId)
    } yield (Unit)
  }

}
