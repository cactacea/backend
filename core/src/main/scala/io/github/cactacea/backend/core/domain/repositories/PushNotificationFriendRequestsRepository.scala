package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationFriendRequestsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId

@Singleton
class PushNotificationFriendRequestsRepository @Inject()(pushNotificationFriendRequestsDAO: PushNotificationFriendRequestsDAO) {

  def find(id: FriendRequestId): Future[Option[List[PushNotification]]] = {
    pushNotificationFriendRequestsDAO.find(id)
  }

  def update(id: FriendRequestId): Future[Unit] = {
    pushNotificationFriendRequestsDAO.update(id)
  }

}
