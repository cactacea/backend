package io.github.cactacea.backend.auth.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.services.IdentityService

class AuthenticationsRepository @Inject()(authenticationsDAO: AuthenticationsDAO) extends IdentityService[Authentication] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[Authentication]] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey)
      .flatMap(_ match {
        case Some(a) =>
          Future.value(Option(a))
        case None =>
          Future.None
      })
  }

  def find(loginInfo: LoginInfo): Future[Option[Authentication]] = {
    authenticationsDAO.find(loginInfo.providerId, loginInfo.providerKey)
  }

  def confirm(loginInfo: LoginInfo): Future[Unit] = {
    authenticationsDAO.updateConfirm(loginInfo.providerId, loginInfo.providerKey, true)
  }

}
