package io.github.cactacea.backend.core.domain.repositories

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._

@Singleton
class NotificationsRepository @Inject()(
                                         notificationsDAO: NotificationsDAO
                                       ) {

  def find(since: Option[Long], offset: Int, count: Int, locales: Seq[Locale], sessionId: SessionId): Future[Seq[Notification]] = {
    notificationsDAO.find(since, offset, count, locales, sessionId)
  }

  def updateReadStatus(notifications: Seq[Notification], sessionId: SessionId): Future[Unit] = {
    if (notifications.size == 0) {
      Future.Unit
    } else {
      notificationsDAO.updateReadStatus(notifications.map(_.id), sessionId)
    }
  }

}
