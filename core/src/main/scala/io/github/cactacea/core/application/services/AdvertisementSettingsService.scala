package io.github.cactacea.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.AdvertisementSetting
import io.github.cactacea.core.domain.repositories.AdvertisementSettingsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.services.DatabaseService

class AdvertisementSettingsService {

  @Inject private var advertisementSettingsRepository: AdvertisementSettingsRepository = _
  @Inject private var db: DatabaseService = _

  def find(sessionId: SessionId): Future[AdvertisementSetting] = {
    advertisementSettingsRepository.find(sessionId)
  }

  def update(setting: AdvertisementSetting, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      advertisementSettingsRepository.update(
        setting.ad1,
        setting.ad2,
        setting.ad3,
        setting.ad4,
        setting.ad5,
        sessionId)
    }
  }

}
