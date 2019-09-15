package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Notification
import io.github.cactacea.backend.core.infrastructure.dao.NotificationInvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId

@Singleton
class NotificationInvitationsRepository @Inject()(notificationInvitationsDAO: NotificationInvitationsDAO) {

  def find(id: InvitationId): Future[Option[Seq[Notification]]] = {
    notificationInvitationsDAO.find(id)
  }

  def update(id: InvitationId): Future[Unit] = {
    notificationInvitationsDAO.update(id)
  }

}
