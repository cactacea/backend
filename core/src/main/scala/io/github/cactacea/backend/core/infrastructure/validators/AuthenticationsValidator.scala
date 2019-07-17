package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class AuthenticationsValidator @Inject()(
                                          authenticationsDAO: AuthenticationsDAO
                           ) {

  def findAccountId(providerId: String, providerKey: String): Future[AccountId] = {
    authenticationsDAO.find(providerId, providerKey).flatMap(_ match {
      case Some(a) =>
        a.accountId match {
          case Some(id) =>
            Future.value(id)
          case None =>
            Future.exception(CactaceaException(AccountNotFound))
        }
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })

  }


}


