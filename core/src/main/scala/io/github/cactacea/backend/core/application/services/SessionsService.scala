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

  def signUp(accountName: String, displayName: Option[String], password: String, udid: String, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], userAgent: Option[String], deviceType: DeviceType): Future[Account] = {
    db.transaction {
      for {
        a <- sessionsRepository.signUp(accountName, displayName, password, udid, deviceType, web, birthday, location, bio, userAgent)
        _ <- actionService.signedUp(a)
      } yield (a)
    }
  }

  def signIn(accountName: String, password: String, udid: String, userAgent: Option[String], deviceType: DeviceType): Future[Account] = {
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
        r <- sessionsRepository.signOut(udid, sessionId)
        _ <- actionService.signedOut(sessionId)
      } yield (r)
    }
  }

}
