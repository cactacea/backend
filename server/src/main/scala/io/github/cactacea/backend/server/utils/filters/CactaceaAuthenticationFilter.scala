package io.github.cactacea.backend.server.utils.filters

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.server.utils.contexts.AuthenticationContext
import io.github.cactacea.backend.core.domain.repositories.UsersRepository
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.Authorization
import io.github.cactacea.filhouette.api.actions.{SecuredActionBuilder, SecuredRequest}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

class CactaceaAuthenticationFilter @Inject()(@Assisted auth: Authorization[Authentication, JWTAuthenticator],
                                             usersRepository: UsersRepository,
                                             securedActionBuilder: SecuredActionBuilder[Authentication, JWTAuthenticator]
                                  ) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val block = (securedRequest: SecuredRequest[Authentication, JWTAuthenticator]) => {
      CactaceaContext.setAuth(securedRequest.identity)
      CactaceaContext.setScope(AuthenticationContext.scope)
      val expiresIn = securedRequest.authenticator.expirationDateTime.getMillis
      securedRequest.identity.userId match {
        case Some(userId) =>
          usersRepository.find(userId, expiresIn).flatMap(_ match {
            case Some(u) =>
              CactaceaContext.setUser(u)
              service(request)
            case None =>
              service(request)
          })
        case None =>
          service(request)
      }
    }
    securedActionBuilder(auth).invokeBlock(request, block)
  }

}
