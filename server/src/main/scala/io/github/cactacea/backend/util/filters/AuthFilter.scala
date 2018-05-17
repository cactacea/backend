package io.github.cactacea.backend.util.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ConfigService
import io.github.cactacea.backend.util.auth.{AuthTokenGenerator, SessionContext}
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository

@Singleton
class AuthFilter @Inject()(sessionsRepository: SessionsRepository, authTokenGenerator: AuthTokenGenerator, configService: ConfigService) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    SessionContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authTokenGenerator.parse(request.headerMap.get("X-AUTHORIZATION")).flatMap({ auth =>
          val expiresIn = auth.expiresIn - configService.basePointInTime
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
