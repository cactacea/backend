package io.github.cactacea.backend.auth.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao.AccountsDAO
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.services.IdentityService

class UsersRepository @Inject()(
                                    accountsDAO: AccountsDAO
                                  ) extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    accountsDAO.find(loginInfo.providerId, loginInfo.providerKey).map(_.map(User(_)))
  }

}
