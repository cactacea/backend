package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.validators.AuthenticationsValidator


class DevicesRepository @Inject()(
                                   authenticationsValidator: AuthenticationsValidator,
                                   devicesDAO: DevicesDAO
                                 ) {

  def create(udid: String, deviceType: DeviceType, userAgent: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.create(udid, deviceType, userAgent, sessionId)
  }


  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, pushToken, sessionId).flatMap(_ => Future.Unit)
  }

  def update(udid: String, deviceStatus: ActiveStatusType, sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, deviceStatus, sessionId).flatMap(_ => Future.Unit)
  }

}
