package io.github.cactacea.core.util.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.core.domain.repositories.SessionsRepository
import io.github.cactacea.core.util.auth.{SessionContext}
import io.github.cactacea.core.util.tokens.AuthTokenGenerator

@Singleton
class AuthFilter @Inject()(sessionsRepository: SessionsRepository, authTokenGenerator: AuthTokenGenerator) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    SessionContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authTokenGenerator.parse(request.headerMap.get("X-AUTHORIZATION")).flatMap({ auth =>
          sessionsRepository.checkAccountStatus(auth.sessionId, auth.expiresIn).flatMap({ _ =>
            SessionContext.setAuthenticated(true)
            SessionContext.setId(auth.sessionId)
            SessionContext.setUdid(auth.sessionUdid)
            service(request)
          })
        })
    }
  }

}

