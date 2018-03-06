package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, SocialAccountsService}
import io.github.cactacea.core.domain.models._
import io.github.cactacea.core.domain.repositories.SessionsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.services.DatabaseService
import org.joda.time.DateTime

@Singleton
class SessionsService @Inject()(
                                 db: DatabaseService,
                                 sessionsRepository: SessionsRepository,
                                 socialAccountsService: SocialAccountsService,
                                 actionService: InjectionService) {

  def signUp(accountName: String, displayName: String, password: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    for {
      a <- db.transaction(sessionsRepository.signUp(accountName, displayName, password, udid, web, birthday, location, bio, userAgent))
      _ <- actionService.signedUp(a)
    } yield (a)
  }

  def signIn(accountName: String, password: String, udid: String, userAgent: String): Future[Authentication] = {
    for {
      a <- db.transaction(sessionsRepository.signIn(accountName, password, udid, userAgent))
      _ <- actionService.signedIn(a)
    } yield (a)
  }

  def signOut(udid: String, sessionId: SessionId): Future[Boolean] = {
    for {
      r <- db.transaction {sessionsRepository.signOut(udid, sessionId)}
      _ <- actionService.signedOut(sessionId)
    } yield (r)
  }

  def signUp(socialAccountType: String, accountName: String, displayName: String, password: String, accessTokenKey: String, accessTokenSecret: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    for {
      id <- socialAccountsService.get(socialAccountType, accessTokenKey, accessTokenSecret)
      a <- db.transaction(sessionsRepository.signUp(socialAccountType, accountName, displayName, password, id, accessTokenKey, accessTokenSecret, udid, web, birthday, location, bio, userAgent))
      _ <- actionService.signedUp(a)
    } yield (a)
  }

  def signIn(socialAccountType: String, accessTokenKey: String, accessTokenSecret: String, udid: String, userAgent: String): Future[Authentication] = {
    for {
      id <- socialAccountsService.get(socialAccountType, accessTokenKey, accessTokenSecret)
      a <- db.transaction(sessionsRepository.signIn(socialAccountType, id, accessTokenKey, accessTokenSecret, udid, userAgent))
      _ <- actionService.signedIn(a)
    } yield (a)
  }

}
