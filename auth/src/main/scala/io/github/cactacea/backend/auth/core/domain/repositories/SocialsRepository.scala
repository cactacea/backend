package io.github.cactacea.backend.auth.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.impl.providers.OAuth2Info
import io.github.cactacea.filhouette.persistence.daos.DelegableAuthInfoDAO

class SocialsRepository @Inject()(authenticationsDAO: AuthenticationsDAO)
  extends DelegableAuthInfoDAO[OAuth2Info] {

  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    authenticationsDAO.create(loginInfo.providerId, loginInfo.providerKey, authInfo.accessToken, "").map(_ => authInfo)
  }

  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey).map(_ match {
      case Some(a) if (a.confirm) =>
        Some(OAuth2Info(a.password))
      case _ => None
    })
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    authenticationsDAO.delete(loginInfo.providerId, loginInfo.providerKey)
  }

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    authenticationsDAO.update(loginInfo.providerId, loginInfo.providerKey, authInfo.accessToken, "").map(_ => authInfo)
  }

  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey).flatMap(_ match {
      case Some(_) =>
        update(loginInfo, authInfo)
      case None =>
        add(loginInfo, authInfo)
    })
  }

}
