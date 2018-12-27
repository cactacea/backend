package io.github.cactacea.backend.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{NotificationService, PushNotificationService}
import io.github.cactacea.backend.core.domain.repositories.{NotificationsRepository, PushNotificationsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultNotificationService @Inject()(
                                            db: DatabaseService,
                                            pushNotificationService: PushNotificationService,
                                            pushNotificationsRepository: PushNotificationsRepository,
                                            notificationsRepository: NotificationsRepository
                                          ) extends NotificationService {

  def fanOutFeed(id: FeedId): Future[Unit] = {
    db.transaction{
      for {
        p <- pushNotificationsRepository.findByFeedId(id)
        a <- pushNotificationService.send(p)
        _ <- pushNotificationsRepository.updateFeedNotified(id, a)
        _ <- notificationsRepository.createFeed(id, a)
      } yield (Unit)
    }
  }

  def fanOutComment(id: CommentId): Future[Unit] = {
    db.transaction{
      for {
        p <- pushNotificationsRepository.findByCommentId(id)
        _ <- pushNotificationService.send(p)
        _ <- pushNotificationsRepository.updateCommentNotified(id)
        _ <- notificationsRepository.createComment(id)
      } yield (Unit)
    }
  }

  def fanOutMessage(id: MessageId): Future[Unit] = {
    db.transaction{
      for {
        p <- pushNotificationsRepository.findByMessageId(id)
        a <- pushNotificationService.send(p)
        _ <- pushNotificationsRepository.updateMessageNotified(id, a)
      } yield (Unit)
    }
  }

  def fanOutGroupInvitation(id: GroupInvitationId): Future[Unit] = {
    for {
      p <- pushNotificationsRepository.findByInvitationId(id)
      _ <- pushNotificationService.send(p)
      _ <- pushNotificationsRepository.updateInvitationNotified(id)
      _ <- notificationsRepository.createInvitation(id)
    } yield (Unit)
  }

  def fanOutFriendRequest(id: FriendRequestId): Future[Unit] = {
    for {
      p <- pushNotificationsRepository.findByRequestId(id)
      _ <- pushNotificationService.send(p)
      _ <- pushNotificationsRepository.updateFriendRequestNotified(id)
      _ <- notificationsRepository.createRequest(id)
    } yield (Unit)
  }

}
