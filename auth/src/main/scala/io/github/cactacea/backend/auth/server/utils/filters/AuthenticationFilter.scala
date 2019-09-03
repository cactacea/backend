package io.github.cactacea.backend.auth.server.utils.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.server.utils.contexts.AuthContext
import io.github.cactacea.filhouette.api.actions.{SecuredActionBuilder, SecuredRequest}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator

@Singleton
class AuthenticationFilter() extends SimpleFilter[Request, Response] {

  @Inject var securedActionBuilder: SecuredActionBuilder[Authentication, JWTAuthenticator] = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val block = (securedRequest: SecuredRequest[Authentication, JWTAuthenticator]) => {
      AuthContext.setAuth(securedRequest.identity)
      service(request)
    }
    securedActionBuilder.invokeBlock(request, block)
  }

}
