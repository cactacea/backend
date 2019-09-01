package io.github.cactacea.backend.auth.application.services

import java.util.Locale

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.domain.repositories.TokensRepository
import io.github.cactacea.backend.auth.enums.TokenType
import io.github.cactacea.backend.auth.utils.mailer.Mailer
import io.github.cactacea.backend.auth.utils.providers.EmailsProvider
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.repositories.{UsersRepository, AuthenticationsRepository}
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.validators.UsersValidator
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.{Credentials, PasswordHasherRegistry}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class EmailAuthenticationService @Inject()(
                                            db: DatabaseService,
                                            response: ResponseBuilder,
                                            usersValidator: UsersValidator,
                                            usersRepository: UsersRepository,
                                            authenticationsRepository: AuthenticationsRepository,
                                            authInfoRepository: AuthInfoRepository,
                                            tokensRepository: TokensRepository,
                                            emailsProvider: EmailsProvider,
                                            mailer: Mailer,
                                            passwordHasherRegistry: PasswordHasherRegistry,
                                            authenticatorService: JWTAuthenticatorService
                               ) {

  import db._

  def register(email: String, locale: Locale): Future[Unit] = {
    transaction {
      for {
        t <- tokensRepository.issue(EmailsProvider.ID, email, TokenType.signUp)
        _ <- mailer.welcome(email, t, locale)
      } yield (())
    }
  }

  def register(email: String, password: String, locale: Locale): Future[Unit] = {
    val l = LoginInfo(EmailsProvider.ID, email)
    transaction {
      for {
        _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
        t <- tokensRepository.issue(EmailsProvider.ID, email, TokenType.signUp)
        _ <- mailer.welcome(email, t, locale)
      } yield (())
    }
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

  def signUp(userName: String, token: String)(implicit request: Request): Future[Response] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, TokenType.signUp)
        _ <- authenticationsRepository.findUserId(l.providerId, l.providerKey)
        a <- usersRepository.create(userName)
        _ <- authenticationsRepository.link(l.providerId, l.providerKey, a.id.sessionId)
        s <- authenticatorService.create(l)
        c <- authenticatorService.init(s)
        r <- authenticatorService.embed(c, response.ok)
      } yield (r)
    }
  }

  def signUp(userName: String, password: String, token: String)(implicit request: Request): Future[Response] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, TokenType.signUp)
        _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
        a <- usersRepository.create(userName)
        _ <- authenticationsRepository.link(l.providerId, l.providerKey, a.id.sessionId)
        s <- authenticatorService.create(l)
        c <- authenticatorService.init(s)
        r <- authenticatorService.embed(c, response.ok)
      } yield (r)
    }
  }

  def signIn(email: String, password: String)(implicit request: Request): Future[Response] = {
    transaction {
      for {
        l <- emailsProvider.authenticate(Credentials(email, password))
        s <- authenticatorService.create(l)
        c <- authenticatorService.init(s)
        r <- authenticatorService.embed(c, response.ok)
      } yield (r)
    }
  }

  def changePassword(password: String, sessionId: SessionId): Future[Unit] = {
    for {
      a <- usersValidator.mustFind(sessionId)
      _ <- db.transaction(authInfoRepository.update(LoginInfo(EmailsProvider.ID, a.displayName), passwordHasherRegistry.current.hash(password)))
    } yield (())
  }

  def recoverPassword(email: String, locale: Locale): Future[Unit] = {
    transaction {
      authenticationsRepository.find(EmailsProvider.ID, email).flatMap(_ match {
        case Some(_) =>
          for {
            t <- tokensRepository.issue(CredentialsProvider.ID, email, TokenType.resetPassword)
            _ <- mailer.forgotPassword(email, t, locale)
          } yield (())
        case None =>
          Future.Unit
      })
    }
  }

  def resetPassword(token: String, password: String): Future[Unit] = {
    transaction {
      for {
        l <- tokensRepository.verify(token, TokenType.resetPassword)
        _ <- authenticationsRepository.exists(l.providerId, l.providerKey)
        _ <- authInfoRepository.update(l, passwordHasherRegistry.current.hash(password))
      } yield (())
    }
  }


}
