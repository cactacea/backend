package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class SessionsService @Inject()(
                                 db: DatabaseService,
                                 sessionsRepository: SessionsRepository,
                                 actionService: InjectionService
                               ) {

  def signUp(accountName: String,
             password: String,
             udid: String,
             userAgent: Option[String],
             deviceType: DeviceType): Future[AccountDetail] = {

    db.transaction {
      for {
        a <- sessionsRepository.signUp(accountName, password, udid, deviceType, userAgent)
        _ <- actionService.signedUp(a)
      } yield (a)
    }
  }

  def signIn(accountName: String, password: String, udid: String, userAgent: Option[String], deviceType: DeviceType): Future[AccountDetail] = {
    db.transaction {
      for {
        a <- sessionsRepository.signIn(accountName, password, udid, deviceType, userAgent)
        _ <- actionService.signedIn(a)
      } yield (a)
    }
  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- sessionsRepository.signOut(udid, sessionId)
        _ <- actionService.signedOut(sessionId)
      } yield (Unit)
    }
  }

}
