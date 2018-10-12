package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.{PushNotificationSetting}
import io.github.cactacea.backend.core.domain.repositories.PushNotificationSettingsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class SettingsService {

  @Inject private var notificationSettingsRepository: PushNotificationSettingsRepository = _
  @Inject private var injectionService: InjectionService = _
  @Inject private var db: DatabaseService = _

  def findPushNotificationSettings(sessionId: SessionId): Future[PushNotificationSetting] = {
    notificationSettingsRepository.find(sessionId)
  }

  def updatePushNotificationSettings(groupInvitation: Boolean, followerFeed: Boolean, feedComment: Boolean, groupMessage: Boolean, directMessage: Boolean, showMessage: Boolean, sessionId: SessionId): Future[Unit] = {
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
