package io.github.cactacea.backend.server.utils.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.models.Auth
import io.github.cactacea.backend.core.domain.repositories.UsersRepository
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.api.actions.{SecuredActionBuilder, SecuredRequest}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

@Singleton
class CactaceaAuthenticationFilter(auth: Authorization[Auth, JWTAuthenticator]) extends SimpleFilter[Request, Response] {

  @Inject var usersRepository: UsersRepository = _
  @Inject var securedActionBuilder: SecuredActionBuilder[Auth, JWTAuthenticator] = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val block = (securedRequest: SecuredRequest[Auth, JWTAuthenticator]) => {
      val sessionId = securedRequest.identity.id.sessionId
      val expiresIn = securedRequest.authenticator.expirationDateTime.getMillis
      usersRepository.find(sessionId, expiresIn).flatMap({ a =>
        CactaceaContext.setUser(a)
        service(request)
      })
    }
    securedActionBuilder(auth).invokeBlock(request, block)
  }

}
