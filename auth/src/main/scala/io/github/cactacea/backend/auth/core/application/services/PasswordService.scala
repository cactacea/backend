package io.github.cactacea.backend.auth.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.repositories.{AuthenticationsRepository, TokensRepository}
import io.github.cactacea.backend.auth.core.utils.mailer.Mailer
import io.github.cactacea.backend.auth.core.utils.providers.EmailsProvider
import io.github.cactacea.backend.auth.enums.AuthTokenType
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.utils.RequestImplicits._
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.PasswordHasherRegistry
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService

@Singleton
class PasswordService @Inject()(
                                 db: DatabaseService,
                                 response: ResponseBuilder,
                                 authenticationsRepository: AuthenticationsRepository,
                                 authInfoRepository: AuthInfoRepository,
                                 tokensRepository: TokensRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry,
                                 authenticatorService: JWTAuthenticatorService,
                                 mailer: Mailer
                               ) {

  import db._

  def changePassword(providerId: String, providerKey: String, newPassword: String)(implicit request: Request): Future[Response] = {
    transaction {
      for {
        _ <- authInfoRepository.update(LoginInfo(providerId, providerKey), passwordHasherRegistry.current.hash(newPassword))
        a <- authenticatorService.create(LoginInfo(providerId, providerKey))
        r <- authenticatorService.renew(a, response.ok)
      } yield (r)
    }
  }

  def recoverPassword(email: String)(implicit request: Request): Future[Response] = {
    transaction {
      authenticationsRepository.find(EmailsProvider.ID, email).flatMap(_ match {
        case Some(_) =>
          for {
            t <- tokensRepository.issue(EmailsProvider.ID, email, AuthTokenType.resetPassword)
            _ <- mailer.forgotPassword(email, t, request.currentLocale())
          } yield (response.ok)
        case None =>
          Future.value(response.ok)
      })
    }
  }

  def resetPassword(token: String, password: String)(implicit request: Request): Future[Response] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, AuthTokenType.resetPassword)
        _ <- authInfoRepository.update(l, passwordHasherRegistry.current.hash(password))
        a <- authenticatorService.create(l)
        r <- authenticatorService.renew(a, response.ok)
      } yield (r)
    }
  }

}
