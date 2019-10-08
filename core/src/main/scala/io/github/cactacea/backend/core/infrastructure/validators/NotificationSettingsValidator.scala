package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.NotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.NotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.UserNotFound

class NotificationSettingsValidator @Inject()(notificationSettingsDAO: NotificationSettingsDAO) {

  def mustFind(sessionId: SessionId): Future[NotificationSetting] = {
    notificationSettingsDAO.find(sessionId).flatMap(_ match {
      case Some(s) =>
        Future.value(NotificationSetting(s))
      case None =>
        Future.exception(CactaceaException(UserNotFound))
    })
  }
}