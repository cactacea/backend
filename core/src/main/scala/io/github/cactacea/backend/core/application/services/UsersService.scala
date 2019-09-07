package io.github.cactacea.backend.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.domain.models.{User, UserStatus}
import io.github.cactacea.backend.core.domain.repositories.UsersRepository
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId, UserId}

@Singleton
class UsersService @Inject()(
                                 usersRepository: UsersRepository,
                                 databaseService: DatabaseService
                               ) {

  import databaseService._

  def create(providerId: String, providerKey: String, userName: String, displayName: Option[String]): Future[User] = {
    usersRepository.create(providerId, providerKey, userName, displayName)
  }

  def find(sessionId: SessionId): Future[User] = {
    usersRepository.find(sessionId)
  }

  def find(userId: UserId, sessionId: SessionId): Future[User] = {
    usersRepository.find(userId, sessionId)
  }

  def isRegistered(userName: String): Future[Boolean] = {
    usersRepository.isRegistered(userName)
  }

  def find(displayName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId) : Future[List[User]]= {

    usersRepository.find(displayName, since, offset, count, sessionId)
  }

  def updateDisplayName(userId: UserId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      usersRepository.updateDisplayName(userId, displayName, sessionId)
    }
  }

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    transaction {
      usersRepository.updateProfile(displayName, web, birthday, location, bio, sessionId)
    }
  }

  def updateProfileImage(profileImage: MediumId, sessionId: SessionId): Future[Unit] = {
    transaction {
      usersRepository.updateProfileImage(Some(profileImage), sessionId)
    }
  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    transaction {
      usersRepository.signOut(udid, sessionId)
    }
  }

  def deleteProfileImage(sessionId: SessionId): Future[Unit] = {
    transaction {
      usersRepository.updateProfileImage(None, sessionId)
    }
  }

  def findUserStatus(userId: UserId, sessionId: SessionId): Future[UserStatus] = {
    usersRepository.findActiveStatus(userId, sessionId)
  }

  def report(userId: UserId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    transaction {
      usersRepository.report(userId, reportType, reportContent, sessionId)
    }
  }

}
