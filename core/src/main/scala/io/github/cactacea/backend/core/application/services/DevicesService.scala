package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ActiveStatus
import io.github.cactacea.backend.core.domain.repositories.DevicesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesService @Inject()(
                                db: DatabaseService,
                                deviceTokensRepository: DevicesRepository,
                                actionService: InjectionService
                              ) {

  def update(pushToken: Option[String], sessionId: SessionId, udid: String): Future[Unit] = {
    db.transaction {
      for {
        _ <- deviceTokensRepository.update(udid, pushToken, sessionId)
        _ <- actionService.devicePushTokenUpdated(sessionId)
      } yield (Unit)
    }
  }

  def update(deviceStatus: ActiveStatus, sessionId: SessionId, udid: String): Future[Unit] = {
    db.transaction {
      for {
        _ <- deviceTokensRepository.update(udid, deviceStatus, sessionId)
        _ <- actionService.deviceStatusUpdated(sessionId)
      } yield (Unit)
    }
  }

}
