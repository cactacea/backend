package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.NotificationSetting
import io.github.cactacea.backend.core.infrastructure.dao.NotificationSettingsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.validators.NotificationSettingsValidator

@Singleton
class NotificationSettingsRepository @Inject()(
                                                notificationSettingsValidator: NotificationSettingsValidator,
                                                notificationSettingsDAO: NotificationSettingsDAO) {

  def find(sessionId: SessionId): Future[NotificationSetting] = {
    notificationSettingsValidator.mustFind(sessionId)
  }

  def update(tweet: Boolean,
             comment: Boolean,
             friendRequest: Boolean,
             message: Boolean,
             channelMessage: Boolean,
             invitation: Boolean,
             showMessage: Boolean,
             sessionId: SessionId): Future[Unit] = {

    notificationSettingsDAO.update(
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
