package io.github.cactacea.backend.utils.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.repositories.{AccountsRepository, DevicesRepository}
import io.github.cactacea.filhouette.api.exceptions.ProviderException
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService
import io.github.cactacea.filhouette.impl.providers.{CommonSocialProfileBuilder, SocialProvider, SocialProviderRegistry}

@Singleton
class SocialAuthenticationsService @Inject()(
                                              db: DatabaseService,
                                              response: ResponseBuilder,
                                              accountsRepository: AccountsRepository,
                                              authInfoRepository: AuthInfoRepository,
                                              devicesRepository: DevicesRepository,
                                              authenticatorService: JWTAuthenticatorService,
                                              socialProviderRegistry: SocialProviderRegistry,
                                              listenerService: ListenerService
                               ) {

  def signIn(provider: String,
             accountName: String,
             udid: String,
             userAgent: Option[String],
             deviceType: DeviceType)(implicit request: Request): Future[Response] = {

    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.value(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            a <- accountsRepository.save(profile.loginInfo.providerId, profile.loginInfo.providerKey, accountName, profile.fullName)
            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            _ <- accountsRepository.link(profile.loginInfo.providerId, profile.loginInfo.providerKey, a.id)
            _ <- devicesRepository.save(profile.loginInfo.providerId, profile.loginInfo.providerKey, udid, deviceType, userAgent)
            s <- authenticatorService.create(profile.loginInfo)
            c <- authenticatorService.init(s)
            r <- authenticatorService.embed(c, response.ok(a))
          } yield (r)
        }
      case _ => Future.exception(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    })
  }

}
