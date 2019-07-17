package io.github.cactacea.backend.utils.filters

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.github.cactacea.backend.utils.models.User
import io.github.cactacea.filhouette.api.actions.{SecuredActionBuilder, SecuredRequest}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

class CactaceaAuthenticationFilter @Inject()(accountsRepository: AccountsRepository,
                                             securedAction: SecuredActionBuilder[User, JWTAuthenticator]
                                  ) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val block = (securedRequest: SecuredRequest[User, JWTAuthenticator]) => {
      val sessionId = securedRequest.identity.id.toSessionId
      val expiresIn = securedRequest.authenticator.expirationDateTime.getMillis
      accountsRepository.find(sessionId, expiresIn).flatMap({ a =>
        CactaceaContext.setAuthenticated(true)
        CactaceaContext.setAccount(a)
        service(request)
      })
    }
    securedAction.invokeBlock(request, block)
  }

}
