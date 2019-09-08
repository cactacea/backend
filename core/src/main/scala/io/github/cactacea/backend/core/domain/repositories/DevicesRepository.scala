package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesRepository @Inject()(
                                   devicesDAO: DevicesDAO
                                 ) {

  def create(udid: String, pushToken: Option[String], deviceType: DeviceType, userAgent: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.exists(udid, sessionId).flatMap(_ match {
      case true =>
        devicesDAO.update(udid, pushToken, sessionId)
      case false =>
        devicesDAO.create(udid, pushToken, deviceType, userAgent, sessionId)
    })
  }

}
