package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.{InjectionService, SocialAccountsService}
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId

@Singleton
class SessionsService {

  @Inject private var db: DatabaseService = _
  @Inject private var sessionsRepository: SessionsRepository = _
  @Inject private var socialAccountsService: SocialAccountsService = _
  @Inject private var actionService: InjectionService = _

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

  def signOut(udid: String, sessionId: SessionId): Future[Boolean] = {
    db.transaction {
      for {
        r <- sessionsRepository.signOut(udid, sessionId)
        _ <- actionService.signedOut(sessionId)
      } yield (r)
    }
  }

  def signUp(socialAccountType: String, accountName: String, displayName: Option[String], password: String, socialAccountIdentifier: String, authenticationCode: String, udid: String, web: Option[String], birthday: Option[Long], location: Option[String], bio: Option[String], userAgent: Option[String], deviceType: DeviceType): Future[Account] = {
    db.transaction {
      for {
        id <- validateSocialAccount(socialAccountType, socialAccountIdentifier, authenticationCode)
        a <- sessionsRepository.signUp(socialAccountType, accountName, displayName, password, id, socialAccountIdentifier, authenticationCode, udid, deviceType, web, birthday, location, bio, userAgent)
        _ <- actionService.signedUp(a)
      } yield (a)
    }
  }

  def issueAuthenticationCode(socialAccountType: String, socialAccountIdentifier: String): Future[Unit] = {
    db.transaction {
      for {
        s <- socialAccountsService.getService(socialAccountType)
        r <- s.issueCode(socialAccountIdentifier)
      } yield (r)
    }
    Future.Unit
  }

  def signIn(socialAccountType: String, socialAccountIdentifier: String, authenticationCode: String, udid: String, userAgent: Option[String], deviceType: DeviceType): Future[Account] = {
    db.transaction {
      for {
        id <- validateSocialAccount(socialAccountType, socialAccountIdentifier, authenticationCode)
        a <- sessionsRepository.signIn(socialAccountType, id, socialAccountIdentifier, authenticationCode, udid, deviceType, userAgent)
        _ <- actionService.signedIn(a)
      } yield (a)
    }
  }

  private def validateSocialAccount(socialAccountType: String, socialAccountIdentifier: String, authenticationCode: String): Future[String] = {
    for {
      s <- socialAccountsService.getService(socialAccountType)
      id <- s.validateCode(socialAccountIdentifier, authenticationCode)
    } yield (id)
  }

}
