package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationCommentsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId

@Singleton
class PushNotificationCommentsRepository @Inject()(pushNotificationCommentsDAO: PushNotificationCommentsDAO) {

  def find(id: CommentId): Future[Option[Seq[PushNotification]]] = {
    pushNotificationCommentsDAO.find(id)
  }

  def update(id: CommentId): Future[Unit] = {
    pushNotificationCommentsDAO.update(id)
  }


}
