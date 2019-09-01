package io.github.cactacea.backend.server.utils.providers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.Request
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.UsersRepository
import io.github.cactacea.backend.oauth.OAuthDataHandler
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.{LoginInfo, RequestProvider}
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class OAuth2RequestProvider @Inject()(usersRepository: UsersRepository, dataHandler: OAuthDataHandler) extends RequestProvider with OAuth2 {
  def authenticate(request: Request): Future[Option[LoginInfo]] = {
    authorize(request, dataHandler) flatMap { auth =>
      val expiresIn = auth.user.issuedAt.getTime
      usersRepository.find(auth.user.userId.sessionId, expiresIn).map({ a =>
        CactaceaContext.setScope(auth.scope)
        CactaceaContext.setUser(a)
        Option(LoginInfo(CredentialsProvider.ID, a.userName))
      })
    }
  }

  override def id = "oauth2"
}
