package io.github.cactacea.backend.server.utils.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.core.domain.repositories.UsersRepository
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.api.actions.{SecuredActionBuilder, SecuredRequest}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

@Singleton
class CactaceaAuthenticationFilter(auth: Authorization[Authentication, JWTAuthenticator]) extends SimpleFilter[Request, Response] {

  @Inject var usersRepository: UsersRepository = _
  @Inject var securedActionBuilder: SecuredActionBuilder[Authentication, JWTAuthenticator] = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val block = (securedRequest: SecuredRequest[Authentication, JWTAuthenticator]) => {
      val providerId = securedRequest.identity.providerId
      val providerKey = securedRequest.identity.providerKey
      val expiresIn = securedRequest.authenticator.expirationDateTime.getMillis
      usersRepository.find(providerId, providerKey, expiresIn).flatMap( _ match {
        case Some(a) =>
          CactaceaContext.setUser(a)
          service(request)
        case None =>
          service(request)
      })
    }
    securedActionBuilder(auth).invokeBlock(request, block)
  }

}
