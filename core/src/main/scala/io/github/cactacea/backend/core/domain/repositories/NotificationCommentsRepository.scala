package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao.NotificationCommentsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId

@Singleton
class NotificationCommentsRepository @Inject()(notificationCommentsDAO: NotificationCommentsDAO) {

  def find(id: CommentId): Future[Option[Seq[Notification]]] = {
    notificationCommentsDAO.find(id)
  }

  def update(id: CommentId): Future[Unit] = {
    notificationCommentsDAO.update(id)
  }


}
