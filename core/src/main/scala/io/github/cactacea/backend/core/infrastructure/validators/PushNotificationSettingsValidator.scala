package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.AccountNotFound

class PushNotificationSettingsValidator @Inject()(pushNotificationSettingsDAO: PushNotificationSettingsDAO) {

  def mustFind(sessionId: SessionId): Future[PushNotificationSetting] = {
    pushNotificationSettingsDAO.find(sessionId).flatMap(_ match {
      case Some(s) =>
        Future.value(PushNotificationSetting(s))
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }
}