package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.github.cactacea.backend.core.domain.repositories.DevicesRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

class DevicesService @Inject()(
                                db: DatabaseService,
                                deviceTokensRepository: DevicesRepository
                              ) {

  def update(pushToken: Option[String], sessionId: SessionId, udid: String): Future[Unit] = {
    for {
      _ <- db.transaction(deviceTokensRepository.update(udid, pushToken, sessionId))
    } yield (())
  }

  def update(deviceStatus: ActiveStatusType, sessionId: SessionId, udid: String): Future[Unit] = {
    for {
      _ <- db.transaction(deviceTokensRepository.update(udid, deviceStatus, sessionId))
    } yield (())
  }

}
