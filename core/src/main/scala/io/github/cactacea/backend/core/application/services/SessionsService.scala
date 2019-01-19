package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class SessionsService @Inject()(
                                 db: DatabaseService,
                                 sessionsRepository: SessionsRepository,
                                 listenerService: ListenerService
                               ) {

  def signUp(accountName: String,
             password: String,
             udid: String,
             userAgent: Option[String],
             deviceType: DeviceType): Future[AccountDetail] = {

    for {
      a <- db.transaction(sessionsRepository.signUp(accountName, password, udid, deviceType, userAgent))
      _ <- listenerService.signedUp(a)
    } yield (a)

  }

  def signIn(accountName: String, password: String, udid: String, userAgent: Option[String], deviceType: DeviceType): Future[AccountDetail] = {
    for {
      a <- db.transaction(sessionsRepository.signIn(accountName, password, udid, deviceType, userAgent))
      _ <- listenerService.signedIn(a)
    } yield (a)

  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(sessionsRepository.signOut(udid, sessionId))
      _ <- listenerService.signedOut(sessionId)
    } yield (Unit)
  }

}
