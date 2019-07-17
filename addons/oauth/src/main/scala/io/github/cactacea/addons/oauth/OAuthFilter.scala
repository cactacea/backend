package io.github.cactacea.addons.oauth

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.oauth2.OAuthError
import com.twitter.finagle.{OAuth2, Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import io.github.cactacea.backend.utils.context.CactaceaContext

@Singleton
class OAuthFilter @Inject()(
                             dataHandler: OAuthHandler,
                             accountsRepository: AccountsRepository
                           ) extends SimpleFilter[Request, Response] with OAuth2 with Logging {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    CactaceaContext.authenticated match {
      case true =>
        service(request)
      case false =>
        authorize(request, dataHandler) flatMap { auth =>
          val expiresIn = auth.user.expiresIn
          accountsRepository.find(auth.user.accountId.toSessionId, expiresIn).flatMap({ a =>
            CactaceaContext.setAuthenticated(true)
            CactaceaContext.setAccount(a)
            service(request)
          })
        } handle {
          case e: OAuthError => e.toResponse
        }
    }
  }
}


