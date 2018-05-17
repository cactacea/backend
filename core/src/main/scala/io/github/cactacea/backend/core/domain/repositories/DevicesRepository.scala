package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ActiveStatus
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesRepository {

  @Inject private var devicesDAO: DevicesDAO = _

  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, pushToken, sessionId).flatMap(_ => Future.Unit)
  }

  def update(udid: String, deviceStatus: ActiveStatus, sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, deviceStatus, sessionId).flatMap(_ => Future.Unit)
  }

}