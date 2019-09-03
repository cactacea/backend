package io.github.cactacea.backend.auth.core.utils.moduels

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.Request
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.repositories.AuthenticationsRepository
import io.github.cactacea.backend.auth.server.utils.contexts.AuthContext
import io.github.cactacea.backend.oauth.OAuthDataHandler
import io.github.cactacea.filhouette.api.{LoginInfo, RequestProvider}
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class OAuth2RequestProvider @Inject()(authenticationsRepository: AuthenticationsRepository, dataHandler: OAuthDataHandler) extends RequestProvider with OAuth2 {
  def authenticate(request: Request): Future[Option[LoginInfo]] = {
    authorize(request, dataHandler) flatMap { auth =>
      authenticationsRepository.find(CredentialsProvider.ID, auth.user.userName).map(_.map({ a =>
        AuthContext.setScope(auth.scope)
        AuthContext.setAuth(a)
        LoginInfo(a.providerId, a.providerKey)
      }))
    }
  }

  override def id = "oauth2"
}
