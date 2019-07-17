package io.github.cactacea.backend.utils.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.util.PasswordInfo
import io.github.cactacea.filhouette.persistence.daos.DelegableAuthInfoDAO

class PasswordsRepository @Inject()(authenticationsDAO: AuthenticationsDAO)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    authenticationsDAO.create(loginInfo.providerId, loginInfo.providerKey, authInfo.password, authInfo.hasher).map(_ => authInfo)
  }

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey).map(_ match {
      case Some(pwd) if (pwd.confirm) =>
        Some(PasswordInfo(pwd.hasher, pwd.password, None))
      case _ => None
    })
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    authenticationsDAO.delete(loginInfo.providerId, loginInfo.providerKey)
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    authenticationsDAO.update(loginInfo.providerId, loginInfo.providerKey, authInfo.password, authInfo.hasher).map(_ => authInfo)
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey).flatMap(_ match {
      case Some(_) =>
        update(loginInfo, authInfo)
      case None =>
        add(loginInfo, authInfo)
    })
  }

}
