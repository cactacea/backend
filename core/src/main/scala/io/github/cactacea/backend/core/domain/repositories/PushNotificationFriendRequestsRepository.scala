package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationRequestsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId

@Singleton
class PushNotificationFriendRequestsRepository @Inject()(pushNotificationFriendRequestsDAO: PushNotificationRequestsDAO) {

  def find(id: FriendRequestId): Future[Option[Seq[PushNotification]]] = {
    pushNotificationFriendRequestsDAO.find(id)
  }

  def update(id: FriendRequestId): Future[Unit] = {
    pushNotificationFriendRequestsDAO.update(id)
  }

}
