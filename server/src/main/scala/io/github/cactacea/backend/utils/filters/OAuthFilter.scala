package io.github.cactacea.backend.utils.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.oauth2.{OAuthError, OAuthErrorInJson}
import com.twitter.finagle.{OAuth2, Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.OAuthHandler

@Singleton
class OAuthFilter @Inject()(dataHandler: OAuthHandler) extends SimpleFilter[Request, Response] with OAuth2 with OAuthErrorInJson with Logging {

  @Inject private var sessionsRepository: SessionsRepository = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    SessionContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authorize(request, dataHandler) flatMap { auth =>
          val expiresIn = auth.user.expiresIn
          sessionsRepository.checkAccountStatus(auth.user.accountId.toSessionId, expiresIn).flatMap({_ =>
            SessionContext.setAuthenticated(true)
            SessionContext.setId(auth.user.accountId.toSessionId)
            SessionContext.setPermissions(auth.user.permissions)
            service(request)
          })
        } handle {
          case e: OAuthError => handleError(e)
        }
    }
  }
}


