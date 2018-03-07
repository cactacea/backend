package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Notification
import io.github.cactacea.core.infrastructure.dao.NotificationsDAO
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class NotificationsRepository {

  @Inject private var notificationsDAO: NotificationsDAO = _

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Notification]] = {
    notificationsDAO.findAll(since, offset, count, sessionId).map(_.map(Notification(_)))
  }

  def updateReadStatus(notifications: List[Notification], sessionId: SessionId): Future[Unit] = {
    if (notifications.size == 0) {
      Future.Unit
    } else {
      notificationsDAO.updateUnread(notifications.map(_.id), sessionId).flatMap(_ => Future.Unit)
    }
  }

}
