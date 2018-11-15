package io.github.cactacea.backend.utils.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.utils.auth.{AuthTokenGenerator, SessionContext}
import io.github.cactacea.backend.utils.oauth.Permissions

@Singleton
class AuthFilter @Inject()(sessionsRepository: SessionsRepository, authTokenGenerator: AuthTokenGenerator) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    SessionContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authTokenGenerator.parse(request.headerMap.get(Config.auth.headerNames.authorizationKey)).flatMap({ auth =>
          val expiresIn = auth.expiresIn
          sessionsRepository.checkAccountStatus(auth.sessionId, expiresIn).flatMap({ _ =>
            SessionContext.setAuthenticated(true)
            SessionContext.setId(auth.sessionId)
            SessionContext.setUdid(auth.sessionUdid)
            SessionContext.setPermissions(Permissions.all)
            service(request)
          })
        })
    }
  }

}

