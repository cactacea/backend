package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationInvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId


class PushNotificationInvitationsRepository @Inject()(pushNotificationInvitationsDAO: PushNotificationInvitationsDAO) {

  def find(id: InvitationId): Future[Option[List[PushNotification]]] = {
    pushNotificationInvitationsDAO.find(id)
  }

  def update(id: InvitationId): Future[Unit] = {
    pushNotificationInvitationsDAO.update(id)
  }

}
