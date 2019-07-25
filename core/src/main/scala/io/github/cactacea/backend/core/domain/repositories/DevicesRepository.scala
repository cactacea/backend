package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId


class DevicesRepository @Inject()(
                                   devicesDAO: DevicesDAO
                                 ) {

  def create(udid: String, deviceType: DeviceType, userAgent: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.create(udid, deviceType, userAgent, sessionId)
  }


  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, pushToken, sessionId)
  }

  def update(udid: String, deviceStatus: ActiveStatusType, sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, deviceStatus, sessionId)
  }

}
