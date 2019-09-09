package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ReportType, UserStatusType}
import io.github.cactacea.backend.core.domain.models.{User, UserStatus}
import io.github.cactacea.backend.core.infrastructure.dao.{DevicesDAO, PushNotificationSettingsDAO, UserAuthenticationsDAO, UserReportsDAO, UsersDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{MediumsValidator, UsersValidator}

@Singleton
class UsersRepository @Inject()(
                                 usersValidator: UsersValidator,
                                 devicesDAO: DevicesDAO,
                                 usersDAO: UsersDAO,
                                 userAuthenticationsDAO: UserAuthenticationsDAO,
                                 userReportsDAO: UserReportsDAO,
                                 mediumsValidator: MediumsValidator,
                                 notificationSettingsDAO: PushNotificationSettingsDAO
                                  ) {

  def create(providerId: String, providerKey: String, userName: String): Future[UserId] = {
    create(providerId, providerKey, userName, None)
  }

  def create(providerId: String, providerKey: String, userName: String, displayName: Option[String]): Future[UserId] = {
    for {
      _ <- usersValidator.mustNotExist(userName)
      i <- usersDAO.create(userName, displayName.getOrElse(userName))
      _ <- userAuthenticationsDAO.create(i, providerId, providerKey)
      _ <- notificationSettingsDAO.create(i.sessionId)
    } yield (i)
  }

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    usersDAO.updateProfile(displayName, web, birthday, location, bio, sessionId)
  }

  def updateUserStatus(userStatus: UserStatusType, sessionId: SessionId): Future[Unit] = {
    usersDAO.updateUserStatus(userStatus, sessionId)
  }

  def updateDisplayName(userId: UserId, displayName: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- usersDAO.updateDisplayName(userId, displayName, sessionId)
    } yield (())
  }

  def updateProfileImage(profileImage: Option[MediumId], sessionId: SessionId): Future[Unit] = {
    profileImage
      .fold(usersDAO.updateProfileImageUrl(None, None, sessionId).map(_ => ())) { id =>
        for {
          u <- mediumsValidator.mustFind(id, sessionId).map(m => Some(m.uri))
          _ <- usersDAO.updateProfileImageUrl(u, profileImage, sessionId)
        } yield (())
      }
  }

  def find(sessionId: SessionId): Future[User] = {
    usersValidator.mustFind(sessionId)
  }

  def find(userId: UserId, sessionId: SessionId): Future[User] = {
    usersValidator.mustFind(userId, sessionId)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[Seq[User]]= {
    usersDAO.find(userName, since, offset, count, sessionId)
  }

  def find(providerId: String, providerKey: String, expiresIn: Long): Future[Option[User]] = {
    usersValidator.mustFind(providerId, providerKey, expiresIn)
  }

  def find(providerId: String, providerKey: String): Future[User] = {
    usersValidator.mustFind(providerId, providerKey)
  }

  def findActiveStatus(userId: UserId, sessionId: SessionId): Future[UserStatus] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- devicesDAO.findActiveStatus(userId)
    } yield (r)
  }

  def isRegistered(userName: String): Future[Boolean] = {
    usersDAO.exists(userName)
  }


  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersDAO.signOut(sessionId)
      _ <- devicesDAO.delete(udid, sessionId)
    } yield (())
  }

  def report(userId: UserId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- userReportsDAO.create(userId, reportType, reportContent, sessionId)
    } yield (())
  }

}
