package io.github.cactacea.backend.auth.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.repositories.{AccountsRepository, AuthenticationsRepository}
import io.github.cactacea.filhouette.api.exceptions.ProviderException
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService
import io.github.cactacea.filhouette.impl.providers.{CommonSocialProfileBuilder, SocialProvider, SocialProviderRegistry}

@Singleton
class SocialAuthenticationService @Inject()(
                                              db: DatabaseService,
                                              response: ResponseBuilder,
                                              accountsRepository: AccountsRepository,
                                              authInfoRepository: AuthInfoRepository,
                                              authenticationsRepository: AuthenticationsRepository,
                                              authenticatorService: JWTAuthenticatorService,
                                              socialProviderRegistry: SocialProviderRegistry
                               ) {

  import db._

  def signIn(provider: String)(implicit request: Request): Future[Response] = {
    transaction {
      (socialProviderRegistry.get[SocialProvider](provider) match {
        case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
          p.authenticate().flatMap {
            case Left(result) =>
              Future.value(result)
            case Right(authInfo) => {
              for {
                profile <- p.retrieveProfile(authInfo)
                s <- authenticatorService.create(profile.loginInfo)
                c <- authenticatorService.init(s)
                r <- authenticatorService.embed(c, response.ok)
              } yield (r)
            }
          }
        case _ => Future.exception(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
      })
    }
  }


  def signUp(provider: String, accountName: String)(implicit request: Request): Future[Response] = {
    transaction {
      (socialProviderRegistry.get[SocialProvider](provider) match {
        case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
          p.authenticate().flatMap {
            case Left(result) =>
              Future.value(result)
            case Right(authInfo) => {
              for {
                profile <- p.retrieveProfile(authInfo)
                a <- accountsRepository.create(accountName, profile.fullName)
                _ <- authInfoRepository.add(profile.loginInfo, authInfo)
                _ <- authenticationsRepository.confirm(profile.loginInfo.providerId, profile.loginInfo.providerKey)
                _ <- authenticationsRepository.link(profile.loginInfo.providerId, profile.loginInfo.providerKey, a.id.toSessionId)
                s <- authenticatorService.create(profile.loginInfo)
                c <- authenticatorService.init(s)
                r <- authenticatorService.embed(c, response.ok)
              } yield (r)
            }
          }
        case _ => Future.exception(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
      })
    }
  }
}

