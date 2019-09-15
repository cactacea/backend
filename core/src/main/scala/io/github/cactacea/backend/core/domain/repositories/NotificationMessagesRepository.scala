package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao.NotificationMessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{MessageId, UserId}

@Singleton
class NotificationMessagesRepository @Inject()(notificationMessagesDAO: NotificationMessagesDAO) {

  def find(id: MessageId): Future[Option[Seq[Notification]]] = {
    notificationMessagesDAO.find(id)
  }

  def update(id: MessageId): Future[Unit] = {
    notificationMessagesDAO.update(id)
  }

  def update(id: MessageId, userIds: Seq[UserId]): Future[Unit] = {
    notificationMessagesDAO.update(id, userIds)
  }

}
