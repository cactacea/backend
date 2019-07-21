package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationMessagesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MessageId}


class PushNotificationMessagesRepository @Inject()(pushNotificationMessagesDAO: PushNotificationMessagesDAO) {

  def find(id: MessageId): Future[Option[List[PushNotification]]] = {
    pushNotificationMessagesDAO.find(id)
  }

  def update(id: MessageId): Future[Unit] = {
    pushNotificationMessagesDAO.update(id)
  }

  def update(id: MessageId, accountIds: List[AccountId]): Future[Unit] = {
    pushNotificationMessagesDAO.update(id, accountIds)
  }

}
