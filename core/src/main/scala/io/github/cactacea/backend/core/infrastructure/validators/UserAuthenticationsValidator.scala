package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.UserAuthenticationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class UserAuthenticationsValidator @Inject()(
                                   userAuthenticationsDAO: UserAuthenticationsDAO
                           ) {

  // Change userName
  def mustNotExists(providerId: String, providerKey: String): Future[Unit] = {
    userAuthenticationsDAO.exists(providerId, providerKey).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(UserAlreadyExist))
    })
  }

  // Link
  def mustNotExists(userId: UserId, providerId: String, providerKey: String): Future[Unit] = {
    userAuthenticationsDAO.exists(userId, providerId, providerKey).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountAlreadyLinked))
    })
  }

}


