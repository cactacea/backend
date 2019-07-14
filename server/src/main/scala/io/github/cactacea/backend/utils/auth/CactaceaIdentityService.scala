package io.github.cactacea.backend.utils.auth

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.AccountsDAO
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.services.IdentityService

class CactaceaIdentityService @Inject()(
                                    accountsDAO: AccountsDAO
                                  ) extends IdentityService[CactaceaAccount] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[CactaceaAccount]] = {
    accountsDAO.find(loginInfo.providerId, loginInfo.providerKey).map(_.map(CactaceaAccount(_)))
  }

}
