package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.validators.PushNotificationSettingsValidator


class PushNotificationSettingsRepository @Inject()(
                                                    pushNotificationSettingsValidator: PushNotificationSettingsValidator,
                                                    pushNotificationSettingsDAO: PushNotificationSettingsDAO) {

  def find(sessionId: SessionId): Future[PushNotificationSetting] = {
    pushNotificationSettingsValidator.find(sessionId)
  }

  def update(feed: Boolean,
             comment: Boolean,
             friendRequest: Boolean,
             message: Boolean,
             groupMessage: Boolean,
             groupInvitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    pushNotificationSettingsDAO.update(
      feed,
      comment,
      friendRequest,
      message,
      groupMessage,
      groupInvitation,
      showMessage,
      sessionId)
  }


}
