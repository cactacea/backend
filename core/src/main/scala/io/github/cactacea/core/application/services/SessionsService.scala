package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.{InjectionService, SocialAccountsService}
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.models._
import io.github.cactacea.core.domain.repositories.SessionsRepository
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import org.joda.time.DateTime

@Singleton
class SessionsService {

  @Inject private var db: DatabaseService = _
  @Inject private var sessionsRepository: SessionsRepository = _
  @Inject private var socialAccountsService: SocialAccountsService = _
  @Inject private var actionService: InjectionService = _

  def signUp(accountName: String, displayName: String, password: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    db.transaction {
      for {
        a <- sessionsRepository.signUp(accountName, displayName, password, udid, web, birthday, location, bio, userAgent)
        _ <- actionService.signedUp(a)
      } yield (a)
    }
  }

  def signIn(accountName: String, password: String, udid: String, userAgent: String): Future[Authentication] = {
    db.transaction {
      for {
        a <- sessionsRepository.signIn(accountName, password, udid, userAgent)
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

  def signUp(socialAccountType: String, accountName: String, displayName: String, password: String, accessTokenKey: String, accessTokenSecret: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    db.transaction {
      for {
        id <- socialAccountsService.get(socialAccountType, accessTokenKey, accessTokenSecret)
        a <- sessionsRepository.signUp(socialAccountType, accountName, displayName, password, id, accessTokenKey, accessTokenSecret, udid, web, birthday, location, bio, userAgent)
        _ <- actionService.signedUp(a)
      } yield (a)
    }
  }

  def signIn(socialAccountType: String, accessTokenKey: String, accessTokenSecret: String, udid: String, userAgent: String): Future[Authentication] = {
    db.transaction {
      for {
        id <- socialAccountsService.get(socialAccountType, accessTokenKey, accessTokenSecret)
        a <- sessionsRepository.signIn(socialAccountType, id, accessTokenKey, accessTokenSecret, udid, userAgent)
        _ <- actionService.signedIn(a)
      } yield (a)
    }
  }

}
