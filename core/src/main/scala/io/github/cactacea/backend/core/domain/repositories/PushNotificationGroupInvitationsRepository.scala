package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationGroupInvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupInvitationId


class PushNotificationGroupInvitationsRepository @Inject()(pushNotificationGroupInvitationsDAO: PushNotificationGroupInvitationsDAO) {

  def find(id: GroupInvitationId): Future[Option[List[PushNotification]]] = {
    pushNotificationGroupInvitationsDAO.find(id)
  }

  def update(id: GroupInvitationId): Future[Unit] = {
    pushNotificationGroupInvitationsDAO.update(id)
  }

}
