package io.github.cactacea.core.util.filters

import com.google.inject.Inject
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.core.domain.repositories.SessionRepository
import io.github.cactacea.core.util.auth.{AuthUserContext, RequestContext}
import io.github.cactacea.core.util.tokens.AuthTokenGenerator

class AuthFilter extends SimpleFilter[Request, Response] {

  @Inject var sessionRepository: SessionRepository = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    RequestContext.authorized match {
      case true =>
        service(request)
      case false =>
        AuthTokenGenerator.parse(request.headerMap.get("X-AUTHORIZATION")).flatMap({ auth =>
          sessionRepository.checkAccountStatus(auth.sessionId, auth.expiresIn).flatMap({ _ =>
            RequestContext.setAuthorized(true)
            AuthUserContext.setId(request, auth.sessionId)
            AuthUserContext.setUdid(request, auth.sessionUdid)
            service(request)
          })
        })
    }
  }

}

