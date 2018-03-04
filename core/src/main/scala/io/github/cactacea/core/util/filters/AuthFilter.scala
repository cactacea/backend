package io.github.cactacea.core.util.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.core.domain.repositories.SessionRepository
import io.github.cactacea.core.util.auth.{SessionContext, AuthenticationContext}
import io.github.cactacea.core.util.tokens.AuthTokenGenerator

@Singleton
class AuthFilter extends SimpleFilter[Request, Response] {

  @Inject var sessionRepository: SessionRepository = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    AuthenticationContext.authenticated match {
      case true =>
        service(request)
      case false =>
        AuthTokenGenerator.parse(request.headerMap.get("X-AUTHORIZATION")).flatMap({ auth =>
          sessionRepository.checkAccountStatus(auth.sessionId, auth.expiresIn).flatMap({ _ =>
            AuthenticationContext.setAuthenticated(true)
            SessionContext.setId(request, auth.sessionId)
            SessionContext.setUdid(request, auth.sessionUdid)
            service(request)
          })
        })
    }
  }

}

