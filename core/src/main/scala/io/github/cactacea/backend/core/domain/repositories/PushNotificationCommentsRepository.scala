package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationCommentsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId


class PushNotificationCommentsRepository @Inject()(pushNotificationCommentsDAO: PushNotificationCommentsDAO) {

  def find(id: CommentId): Future[Option[List[PushNotification]]] = {
    pushNotificationCommentsDAO.find(id)
  }

  def update(id: CommentId): Future[Unit] = {
    pushNotificationCommentsDAO.update(id)
  }


}
