package io.github.cactacea.backend.server.utils.providers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.OAuth2
import com.twitter.finagle.http.Request
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.filhouette.api.{LoginInfo, RequestProvider}
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider
import io.github.cactacea.oauth.OAuthDataHandler

@Singleton
class OAuth2RequestProvider @Inject()(accountsRepository: AccountsRepository, dataHandler: OAuthDataHandler) extends RequestProvider with OAuth2 {
  def authenticate(request: Request): Future[Option[LoginInfo]] = {
    authorize(request, dataHandler) flatMap { auth =>
      val expiresIn = auth.user.issuedAt.getTime
      accountsRepository.find(auth.user.accountId.toSessionId, expiresIn).map({ a =>
        CactaceaContext.setAccount(a)
        Option(LoginInfo(CredentialsProvider.ID, a.accountName))
      })
    }
  }

  override def id = "oauth2"
}
