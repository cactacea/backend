package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.domain.repositories.DevicesRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class DevicesService @Inject()(db: DatabaseService, deviceTokensRepository: DevicesRepository, actionService: InjectionService) {

  def update(sessionId: SessionId, udid: String, token: Option[String]): Future[Unit] = {
    for {
      r <- db.transaction(deviceTokensRepository.update(udid, token, sessionId))
      _ <- actionService.deviceUpdated(sessionId)
    } yield (r)
  }

}
