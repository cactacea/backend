package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.repositories.DevicesRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesService @Inject()(db: DatabaseService) {

  @Inject var deviceTokensRepository: DevicesRepository = _

  def update(sessionId: SessionId, udid: String, pushToken: Option[String]): Future[Unit] = {
    db.transaction {
      deviceTokensRepository.update(udid, pushToken, sessionId)
    }
  }

}
