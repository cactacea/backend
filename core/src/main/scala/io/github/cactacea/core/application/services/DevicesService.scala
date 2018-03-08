package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.repositories.DevicesRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesService {

  @Inject private var db: DatabaseService = _
  @Inject private var deviceTokensRepository: DevicesRepository = _
  @Inject private var actionService: InjectionService = _

  def update(token: Option[String], sessionId: SessionId, udid: String): Future[Unit] = {
    db.transaction {
      for {
        r <- deviceTokensRepository.update(udid, token, sessionId)
        _ <- actionService.deviceUpdated(sessionId)
      } yield (r)
    }
  }

}
