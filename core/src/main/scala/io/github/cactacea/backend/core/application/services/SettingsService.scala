package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.core.domain.repositories.PushNotificationSettingsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class SettingsService @Inject()(
                                 notificationSettingsRepository: PushNotificationSettingsRepository,
                                 db: DatabaseService
                               ) {

  def findPushNotificationSettings(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsRepository.find(sessionId)
  }

  def updatePushNotificationSettings(groupInvitation: Boolean,
                                     followerFeed: Boolean,
                                     feedComment: Boolean,
                                     groupMessage: Boolean,
                                     directMessage: Boolean,
                                     showMessage: Boolean,
                                     sessionId: SessionId): Future[Unit] = {
    db.transaction {
      notificationSettingsRepository.update(
        groupInvitation,
        followerFeed,
        feedComment,
        groupMessage,
        directMessage,
        showMessage,
        sessionId)
    }
  }

}
