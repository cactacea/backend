package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.PushNotificationSetting
import io.github.cactacea.core.domain.repositories.PushNotificationSettingsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.services.DatabaseService

class PushNotificationSettingsService @Inject()(db: DatabaseService) {

  @Inject var notificationSettingsRepository: PushNotificationSettingsRepository = _

  def find(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsRepository.find(sessionId)
  }

  def edit(setting: PushNotificationSetting, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      notificationSettingsRepository.update(
        setting.groupInvite,
        setting.followerFeed,
        setting.feedComment,
        setting.groupMessage,
        setting.directMessage,
        setting.showMessage,
        sessionId)
    }
  }

}
