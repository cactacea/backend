package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.NotificationSetting
import io.github.cactacea.backend.core.domain.repositories.NotificationSettingsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class SettingsService @Inject()(
                                 databaseService: DatabaseService,
                                 notificationSettingsRepository: NotificationSettingsRepository
                               ) {

  import databaseService._

  def findNotificationSettings(sessionId: SessionId): Future[NotificationSetting] = {
    notificationSettingsRepository.find(sessionId)
  }

  def updateNotificationSettings(tweet: Boolean,
                                     comment: Boolean,
                                     friendRequest: Boolean,
                                     message: Boolean,
                                     channelMessage: Boolean,
                                     invitation: Boolean,
                                     showMessage: Boolean,
                                     sessionId: SessionId): Future[Unit] = {
    transaction {
      notificationSettingsRepository.update(
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

}
