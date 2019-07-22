package io.github.cactacea.backend.server.utils.filters

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.api.actions.{SecuredActionBuilder, SecuredRequest}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

class CactaceaAuthenticationFilter(auth: Authorization[User, JWTAuthenticator]) extends SimpleFilter[Request, Response] {

  @Inject var accountsRepository: AccountsRepository = _
  @Inject var securedActionBuilder: SecuredActionBuilder[User, JWTAuthenticator] = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val block = (securedRequest: SecuredRequest[User, JWTAuthenticator]) => {
      val sessionId = securedRequest.identity.id.toSessionId
      val expiresIn = securedRequest.authenticator.expirationDateTime.getMillis
      accountsRepository.find(sessionId, expiresIn).flatMap({ a =>
        CactaceaContext.setAccount(a)
        service(request)
      })
    }
    securedActionBuilder(auth).invokeBlock(request, block)
  }

}
