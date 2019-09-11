package io.github.cactacea.backend.auth.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Token
import io.github.cactacea.backend.auth.core.domain.repositories.{AuthenticationsRepository, TokensRepository}
import io.github.cactacea.backend.auth.core.utils.mailer.Mailer
import io.github.cactacea.backend.auth.core.utils.providers.EmailsProvider
import io.github.cactacea.backend.auth.enums.AuthTokenType
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

  def signUp(email: String, password: String)(implicit request: Request): Future[Response] = {
    val l = LoginInfo(emailsProvider.id, email)
    authenticationsRepository.exists(emailsProvider.id, email).flatMap(_ match {
      case true =>
        transaction {
          for {
            _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
            s <- authenticatorService.create(l)
            c <- authenticatorService.init(s)
            t <- tokensRepository.issue(emailsProvider.id, email, AuthTokenType.signUp)
            _ <- mailer.welcome(email, t, request.currentLocale())
          } yield (response.ok.body(Token(email, c)))
        }
      case false =>
        for {
          s <- authenticatorService.create(l)
          c <- authenticatorService.init(s)
          t <- tokensRepository.issue(emailsProvider.id, email, AuthTokenType.signUp)
          _ <- mailer.welcome(email, t, request.currentLocale())
        } yield (response.ok.body(Token(email, c)))
    })
  }

  def signIn(email: String, password: String)(implicit request: Request): Future[Response] = {
    for {
      l <- emailsProvider.authenticate(Credentials(email, password))
      s <- authenticatorService.create(l)
      c <- authenticatorService.init(s)
      r <- authenticatorService.embed(c, response.ok.body(Token(email, c)))
    } yield (r)
  }

  def verify(token: String): Future[Unit] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, AuthTokenType.signUp)
        _ <- authenticationsRepository.confirm(l)
      } yield (())
    }
  }

  def reject(token: String): Future[Unit] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, AuthTokenType.signUp)
        _ <- authInfoRepository.remove(l)
      } yield (())
    }
  }

}

