package io.github.cactacea.backend.auth.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.repositories.{AuthenticationsRepository, TokensRepository}
import io.github.cactacea.backend.auth.core.utils.mailer.Mailer
import io.github.cactacea.backend.auth.core.utils.providers.EmailsProvider
import io.github.cactacea.backend.auth.enums.TokenType
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.utils.RequestImplicits._
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.{Credentials, PasswordHasherRegistry}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService

@Singleton
class EmailAuthenticationService @Inject()(
                                            db: DatabaseService,
                                            response: ResponseBuilder,
                                            authenticationsRepository: AuthenticationsRepository,
                                            authInfoRepository: AuthInfoRepository,
                                            tokensRepository: TokensRepository,
                                            emailsProvider: EmailsProvider,
                                            mailer: Mailer,
                                            passwordHasherRegistry: PasswordHasherRegistry,
                                            authenticatorService: JWTAuthenticatorService
                               ) {

  import db._

  def signUp(email: String, password: String)(implicit request: Request): Future[Unit] = {
    val l = LoginInfo(emailsProvider.id, email)
    authenticationsRepository.notExists(emailsProvider.id, email).flatMap(_ match {
      case true =>
        for {
          t <- tokensRepository.issue(emailsProvider.id, email, TokenType.signUp)
          _ <- mailer.welcome(email, t, request.currentLocale())
        } yield (())
      case false =>
        transaction {
          for {
            _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
            t <- tokensRepository.issue(emailsProvider.id, email, TokenType.signUp)
            _ <- mailer.welcome(email, t, request.currentLocale())
          } yield (())
        }
    })
  }

  def signIn(email: String, password: String)(implicit request: Request): Future[Response] = {
    for {
      l <- emailsProvider.authenticate(Credentials(email, password))
      s <- authenticatorService.create(l)
      c <- authenticatorService.init(s)
      r <- authenticatorService.embed(c, response.ok)
    } yield (r)
  }

  def verify(token: String): Future[Unit] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, TokenType.signUp)
        _ <- authenticationsRepository.confirm(l.providerId, l.providerKey)
      } yield (())
    }
  }

  def reject(token: String): Future[Unit] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, TokenType.signUp)
        _ <- authInfoRepository.remove(l)
      } yield (())
    }
  }

}

