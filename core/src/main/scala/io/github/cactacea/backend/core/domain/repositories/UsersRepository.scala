package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{UserStatusType, ReportType}
import io.github.cactacea.backend.core.domain.models.{User, UserStatus}
import io.github.cactacea.backend.core.infrastructure.dao.{UserReportsDAO, UsersDAO, DevicesDAO, PushNotificationSettingsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, MediumId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{UsersValidator, MediumsValidator}


class UsersRepository @Inject()(
                                 usersDAO: UsersDAO,
                                 usersValidator: UsersValidator,
                                 devicesDAO: DevicesDAO,
                                 userReportsDAO: UserReportsDAO,
                                 mediumsValidator: MediumsValidator,
                                 notificationSettingsDAO: PushNotificationSettingsDAO

                                  ) {

  def create(userName: String): Future[User] = {
    for {
      _ <- usersValidator.mustNotExist(userName)
      i <- usersDAO.create(userName)
      _ <- notificationSettingsDAO.create(i.sessionId)
      a <- usersValidator.mustFind(i.sessionId)
    } yield (a)
  }

  def create(userName: String, displayName: Option[String]): Future[User] = {
    for {
      _ <- usersValidator.mustNotExist(userName)
      i <- usersDAO.create(userName, displayName.getOrElse(userName))
      _ <- notificationSettingsDAO.create(i.sessionId)
      a <- usersValidator.mustFind(i.sessionId)
    } yield (a)
  }

  def find(sessionId: SessionId): Future[User] = {
    usersValidator.mustFind(sessionId)
  }

  // for reset password
  def find(providerId: String, providerKey: String): Future[User] = {
    usersValidator.mustFind(providerId, providerKey)
  }


  def find(userId: UserId, sessionId: SessionId): Future[User] = {
    usersValidator.mustFind(userId, sessionId)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[User]]= {
    usersDAO.find(userName, since, offset, count, sessionId)
  }

  def isRegistered(userName: String): Future[Boolean] = {
    usersDAO.exists(userName)
  }

  def find(sessionId: SessionId, expiresIn: Long): Future[User] = {
    usersValidator.mustFind(sessionId, expiresIn)
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

  def updateProfile(displayName: String,
                    web: Option[String],
                    birthday: Option[Long],
                    location: Option[String],
                    bio: Option[String],
                    sessionId: SessionId): Future[Unit] = {

    usersDAO.updateProfile(displayName, web, birthday, location, bio, sessionId)
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

  def findActiveStatus(userId: UserId, sessionId: SessionId): Future[UserStatus] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- devicesDAO.findActiveStatus(userId)
    } yield (r)
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
