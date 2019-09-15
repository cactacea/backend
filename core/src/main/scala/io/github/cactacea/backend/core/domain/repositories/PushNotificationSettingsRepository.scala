package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.PushNotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.validators.PushNotificationSettingsValidator

@Singleton
class PushNotificationSettingsRepository @Inject()(
                                                    pushNotificationSettingsValidator: PushNotificationSettingsValidator,
                                                    pushNotificationSettingsDAO: PushNotificationSettingsDAO) {

  def find(sessionId: SessionId): Future[PushNotificationSetting] = {
    pushNotificationSettingsValidator.mustFind(sessionId)
  }

  def update(tweet: Boolean,
             comment: Boolean,
             friendRequest: Boolean,
             message: Boolean,
             channelMessage: Boolean,
             invitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    pushNotificationSettingsDAO.update(
      tweet,
      comment,
      friendRequest,
      message,
      channelMessage,
      invitation,
      showMessage,
      sessionId)
  }


}
