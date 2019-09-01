package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationMessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, MessageId}


class PushNotificationMessagesRepository @Inject()(pushNotificationMessagesDAO: PushNotificationMessagesDAO) {

  def find(id: MessageId): Future[Option[List[PushNotification]]] = {
    pushNotificationMessagesDAO.find(id)
  }

  def update(id: MessageId): Future[Unit] = {
    pushNotificationMessagesDAO.update(id)
  }

  def update(id: MessageId, userIds: List[UserId]): Future[Unit] = {
    pushNotificationMessagesDAO.update(id, userIds)
  }

}
