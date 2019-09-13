package io.github.cactacea.backend.auth.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.SessionToken
import io.github.cactacea.backend.auth.core.domain.repositories.AuthenticationsRepository
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.exceptions.{ConfigurationException, ProviderException}
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService
import io.github.cactacea.filhouette.impl.providers._

@Singleton
class SocialAuthenticationService @Inject()(
                                             db: DatabaseService,
                                             response: ResponseBuilder,
                                             authInfoRepository: AuthInfoRepository,
                                             authenticationsRepository: AuthenticationsRepository,
                                             authenticatorService: JWTAuthenticatorService,
                                             socialProviderRegistry: SocialProviderRegistry,
                               ) {

  import db._

  def authenticate(provider: String)(implicit request: Request): Future[Response] = {
    transaction {
      (socialProviderRegistry.get[SocialProvider](provider) match {
        case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
          p.authenticate().flatMap {
            case Left(result) =>
              Future.value(result)
            case Right(authInfo) => {
              for {
                profile <- p.retrieveProfile(authInfo)
                u <- authenticationsRepository.find(profile.loginInfo.providerId, profile.loginInfo.providerKey).map(_.flatMap(_.userId))
                _ <- authInfoRepository.save(profile.loginInfo, authInfo)
                _ <- authenticationsRepository.confirm(profile.loginInfo.providerId, profile.loginInfo.providerKey)
                s <- authenticatorService.create(profile.loginInfo)
                c <- authenticatorService.init(s)
                r <- authenticatorService.embed(c, response.ok.body(SessionToken(profile.loginInfo.providerKey, c, u)))
              } yield (r)
            }
          }
        case _ =>
          Future.exception(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
      })
    }
  }

  def authenticate(provider: String, token: String, expiresIn: Option[Int], secret: Option[String])(implicit request: Request): Future[Response] = {
    transaction {
      (socialProviderRegistry.get[SocialProvider](provider) match {
        case Some(p: OAuth2Provider with CommonSocialProfileBuilder) =>
          val authInfo = OAuth2Info(accessToken = token, expiresIn = expiresIn)
          for {
            profile <- p.retrieveProfile(authInfo)
            u <- authenticationsRepository.find(profile.loginInfo.providerId, profile.loginInfo.providerKey).map(_.flatMap(_.userId))
            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            _ <- authenticationsRepository.confirm(profile.loginInfo.providerId, profile.loginInfo.providerKey)
            s <- authenticatorService.create(profile.loginInfo)
            c <- authenticatorService.init(s)
            r <- authenticatorService.embed(c, response.ok.body(SessionToken(profile.loginInfo.providerKey, c, u)))
          } yield (r)
        case _ =>
          Future.exception(new ConfigurationException(s"Cannot retrive information with unexpected social provider $provider"))
      })
    }
  }

  def link(provider: String, token: String, expiresIn: Option[Int], secret: Option[String], providerId: String, providerKey: String): Future[Unit] = {
    transaction {
      (socialProviderRegistry.get[SocialProvider](provider) match {
        case Some(p: OAuth2Provider with CommonSocialProfileBuilder) => //for OAuth2 provider type
          val authInfo = OAuth2Info(accessToken = token, expiresIn = expiresIn)
          for {
            profile <- p.retrieveProfile(authInfo)
            i <- authenticationsRepository.findUserId(providerId, providerKey)

            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            _ <- authenticationsRepository.link(providerId, providerKey, i)
          } yield (())

        case _ =>
          Future.exception(new ConfigurationException(s"Cannot retrive information with unexpected social provider $provider"))
      })
    }
  }

  def unlink(provider: String, providerId: String, providerKey: String): Future[Unit] = {
    transaction {
      (socialProviderRegistry.get[SocialProvider](provider) match {
        case Some(_: OAuth2Provider with CommonSocialProfileBuilder) => //for OAuth2 provider type
          for {
            i <- authenticationsRepository.findUserId(providerId, providerKey)
            k <- authenticationsRepository.findProviderKey(provider, i)
            _ <- authInfoRepository.remove(LoginInfo(provider, k))
          } yield (())
        case _ =>
          Future.exception(new ConfigurationException(s"Cannot retrive information with unexpected social provider $provider"))
      })
    }
  }

}
