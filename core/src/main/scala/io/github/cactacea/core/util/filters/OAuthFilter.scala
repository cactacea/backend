package io.github.cactacea.core.util.filters

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.oauth2.{OAuthError, OAuthErrorInJson}
import com.twitter.finagle.{OAuth2, Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.cactacea.core.application.services.ScopesService
import io.github.cactacea.core.domain.enums.PermissionType
import io.github.cactacea.core.domain.repositories.SessionRepository
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.auth.AuthenticationContext
import io.github.cactacea.core.util.oauth.OAuthHandler

@Singleton
class OAuthFilter @Inject()(dataHandler: OAuthHandler) extends SimpleFilter[Request, Response] with OAuth2 with OAuthErrorInJson with Logging {

  @Inject var sessionRepository: SessionRepository = _
  @Inject var scopesService: ScopesService = _

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    AuthenticationContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authorize(request, dataHandler) flatMap { auth =>
          sessionRepository.checkAccountStatus(auth.user.accountId.toSessionId, auth.user.expiresIn).flatMap({_ =>
            AuthenticationContext.setAuthenticated(true)
            SessionContext.setId(request, auth.user.accountId.toSessionId)
            SessionContext.setUdid(request, "")
            val permission = request.method match {
              case Method.Get =>
                PermissionType.readOnly
              case _ =>
                PermissionType.readOnly
            }
            scopesService.validate(PermissionType.readOnly, permission).flatMap({ _ =>
              service(request)
            })
          })
        } handle {
          case e: OAuthError => handleError(e)
        }
    }
  }
}