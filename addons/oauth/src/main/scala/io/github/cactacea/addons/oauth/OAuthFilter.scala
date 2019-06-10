package io.github.cactacea.addons.oauth

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.oauth2.OAuthError
import com.twitter.finagle.{OAuth2, Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.SessionsRepository
import io.github.cactacea.backend.utils.auth.CactaceaContext

@Singleton
class OAuthFilter @Inject()(
                             dataHandler: OAuthHandler,
                             sessionsRepository: SessionsRepository
                           ) extends SimpleFilter[Request, Response] with OAuth2 with Logging {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    CactaceaContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authorize(request, dataHandler) flatMap { auth =>
          val expiresIn = auth.user.expiresIn
          sessionsRepository.checkAccountStatus(auth.user.accountId.toSessionId, expiresIn).flatMap({_ =>
            CactaceaContext.setAuthenticated(true)
            CactaceaContext.setId(auth.user.accountId.toSessionId)
            service(request)
          })
        } handle {
          case e: OAuthError => e.toResponse
        }
    }
  }
}


