package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ActiveStatusType, DeviceType}
import io.github.cactacea.backend.core.infrastructure.dao.DevicesDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.AuthenticationsValidator

@Singleton
class DevicesRepository @Inject()(
                                   authenticationsValidator: AuthenticationsValidator,
                                   devicesDAO: DevicesDAO
                                 ) {

  def save(providerId: String, providerKey: String, udid: String, deviceType: DeviceType, userAgent: Option[String]): Future[AccountId] = {
    for {
      a <- authenticationsValidator.findAccountId(providerId, providerKey)
      _ <- devicesDAO.create(udid, deviceType, userAgent, a.toSessionId)
    } yield (a)
  }


  def update(udid: String, pushToken: Option[String], sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, pushToken, sessionId).flatMap(_ => Future.Unit)
  }

  def update(udid: String, deviceStatus: ActiveStatusType, sessionId: SessionId): Future[Unit] = {
    devicesDAO.update(udid, deviceStatus, sessionId).flatMap(_ => Future.Unit)
  }

}
