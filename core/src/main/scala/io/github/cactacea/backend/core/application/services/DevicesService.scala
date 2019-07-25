package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.github.cactacea.backend.core.domain.repositories.DevicesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class DevicesService @Inject()(
                                databaseService: DatabaseService,
                                deviceTokensRepository: DevicesRepository
                              ) {

  import databaseService._

  def update(pushToken: Option[String], sessionId: SessionId, udid: String): Future[Unit] = {
    transaction{
      deviceTokensRepository.update(udid, pushToken, sessionId)
    }
  }

  def update(deviceStatus: ActiveStatusType, sessionId: SessionId, udid: String): Future[Unit] = {
    transaction {
      deviceTokensRepository.update(udid, deviceStatus, sessionId)
    }
  }

}
