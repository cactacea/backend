package io.github.cactacea.backend.core.application.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.repositories.DevicesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class DevicesService @Inject()(
                                databaseService: DatabaseService,
                                devicesRepository: DevicesRepository
                              ) {

  import databaseService._

  def update(udid: String, pushToken: Option[String], deviceType: DeviceType,
             userAgent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      devicesRepository.create(udid, pushToken, deviceType, userAgent,  sessionId)
    }
  }

}
