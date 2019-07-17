package io.github.cactacea.backend.utils.services

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, AuthenticationsDAO}
import io.github.cactacea.backend.utils.models.User
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.services.IdentityService

class UserService @Inject()(
                                    accountsDAO: AccountsDAO,
                                    authenticationsDAO: AuthenticationsDAO
                                  ) extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    accountsDAO.find(loginInfo.providerId, loginInfo.providerKey).map(_.map(User(_)))
  }

}
