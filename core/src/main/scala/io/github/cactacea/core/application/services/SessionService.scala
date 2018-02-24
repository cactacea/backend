package io.github.cactacea.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.SocialAccountType
import io.github.cactacea.core.domain.models._
import io.github.cactacea.core.domain.repositories.SessionRepository
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import org.joda.time.DateTime

@Singleton
class SessionService @Inject()(db: DatabaseService) {

  @Inject var sessionRepository: SessionRepository = _

  def signIn(accountName: String, password: String, udid: String, userAgent: String): Future[Authentication] = {
    db.transaction(
      sessionRepository.signIn(accountName, password, udid, userAgent)
    )
  }

  def signOut(udid: String, sessionId: SessionId): Future[Boolean] = {
    db.transaction {
      sessionRepository.signOut(udid, sessionId)
    }
  }

  def signUp(accountName: String, displayName: String, password: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    db.transaction(
      sessionRepository.signUp(accountName, displayName, password, udid, web, birthday, location, bio, userAgent)
    )
  }

  def signUp(accountType: SocialAccountType, accountName: String, displayName: String, password: String, accessTokenKey: String, accessTokenSecret: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    db.transaction(
      sessionRepository.signUp(accountType, accountName, displayName, password, accessTokenKey, accessTokenSecret, udid, web, birthday, location, bio, userAgent)
    )
  }

  def signIn(accountType: SocialAccountType, accessTokenKey: String, accessTokenSecret: String, udid: String, userAgent: String): Future[Authentication] = {
    db.transaction(
      sessionRepository.signIn(accountType, accessTokenKey, accessTokenSecret, udid, userAgent)
    )
  }

}
