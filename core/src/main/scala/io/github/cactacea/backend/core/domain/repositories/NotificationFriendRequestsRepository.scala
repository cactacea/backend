package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao.NotificationRequestsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId

@Singleton
class NotificationFriendRequestsRepository @Inject()(notificationFriendRequestsDAO: NotificationRequestsDAO) {

  def find(id: FriendRequestId): Future[Option[Seq[Notification]]] = {
    notificationFriendRequestsDAO.find(id)
  }

  def update(id: FriendRequestId): Future[Unit] = {
    notificationFriendRequestsDAO.update(id)
  }

}
