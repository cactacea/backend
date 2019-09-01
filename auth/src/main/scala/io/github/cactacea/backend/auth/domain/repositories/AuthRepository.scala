package io.github.cactacea.backend.auth.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.models.Auth
import io.github.cactacea.backend.core.infrastructure.dao.UsersDAO
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.services.IdentityService

class AuthRepository @Inject()(
                                    usersDAO: UsersDAO
                                  ) extends IdentityService[Auth] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[Auth]] = {
    usersDAO.find(loginInfo.providerId, loginInfo.providerKey)
      .flatMap(_ match {
        case Some(a) =>
          Future.value(Option(Auth(a)))
        case None =>
          Future.None
      })
  }

}
