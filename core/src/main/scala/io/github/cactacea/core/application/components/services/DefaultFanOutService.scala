package io.github.cactacea.core.application.components.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{FanOutService, PushNotificationService}
import io.github.cactacea.core.domain.repositories.{NotificationsRepository, PushNotificationsRepository}
import io.github.cactacea.core.infrastructure.identifiers._

class DefaultFanOutService @Inject()(db: DatabaseService) extends FanOutService {

  @Inject private var pushNotificationService: PushNotificationService = _
  @Inject private var pushNotificationsRepository: PushNotificationsRepository = _
  @Inject private var notificationsRepository: NotificationsRepository = _

  def fanOutFeed(id: FeedId): Future[Unit] = {
    db.transaction{
      for {
        p <- pushNotificationsRepository.findByFeedId(id)
        a <- pushNotificationService.send(p)
        _ <- pushNotificationsRepository.updateFeedNotified(id, a)
        _ <- notificationsRepository.createFeed(id, a)
      } yield (Future.value(Unit))
    }
  }

  def fanOutComment(id: CommentId): Future[Unit] = {
    db.transaction{
      for {
        p <- pushNotificationsRepository.findByCommentId(id)
        _ <- pushNotificationService.send(p)
        _ <- pushNotificationsRepository.updateCommentNotified(id)
        _ <- notificationsRepository.createComment(id)
      } yield (Future.value(Unit))
    }
  }

  def fanOutMessage(id: MessageId): Future[Unit] = {
    db.transaction{
      for {
        p <- pushNotificationsRepository.findByMessageId(id)
        a <- pushNotificationService.send(p)
        _ <- pushNotificationsRepository.updateMessageNotified(id, a)
      } yield (Future.value(Unit))
    }
  }

  def fanOutGroupInvitation(id: GroupInvitationId): Future[Unit] = {
    for {
      p <- pushNotificationsRepository.findByInvitationId(id)
      _ <- pushNotificationService.send(p)
      _ <- pushNotificationsRepository.updateInvitationNotified(id)
      _ <- notificationsRepository.createInvitation(id)
    } yield (Future.value(Unit))
  }

  def fanOutFriendRequest(id: FriendRequestId): Future[Unit] = {
    for {
      p <- pushNotificationsRepository.findByRequestId(id)
      _ <- pushNotificationService.send(p)
      _ <- pushNotificationsRepository.updateFriendRequestNotified(id)
      _ <- notificationsRepository.createRequest(id)
    } yield (Future.value(Unit))
  }

}
