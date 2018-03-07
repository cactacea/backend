package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesRepository {

  @Inject private var devicesDAO: DevicesDAO = _

  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, pushToken, sessionId).flatMap(_ => Future.Unit)
  }


}
