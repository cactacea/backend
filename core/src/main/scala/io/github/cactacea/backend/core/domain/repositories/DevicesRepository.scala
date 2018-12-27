package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ActiveStatusType
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class DevicesRepository @Inject()(
                                   devicesDAO: DevicesDAO
                                 ) {

  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, pushToken, sessionId).flatMap(_ => Future.Unit)
  }

  def update(udid: String, deviceStatus: ActiveStatusType, sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, deviceStatus, sessionId).flatMap(_ => Future.Unit)
  }

}
