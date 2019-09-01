package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.domain.repositories.PushNotificationSettingsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class SettingsService @Inject()(
                                 databaseService: DatabaseService,
                                 notificationSettingsRepository: PushNotificationSettingsRepository
                               ) {

  import databaseService._

  def findPushNotificationSettings(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsRepository.find(sessionId)
  }

  def updatePushNotificationSettings(feed: Boolean,
                                     comment: Boolean,
                                     friendRequest: Boolean,
                                     message: Boolean,
                                     channelMessage: Boolean,
                                     invitation: Boolean,
                                     showMessage: Boolean,
                                     sessionId: SessionId): Future[Unit] = {
    transaction {
      notificationSettingsRepository.update(
        feed,
        comment,
        friendRequest,
        message,
        channelMessage,
        invitation,
        showMessage,
        sessionId)
    }
  }

}
