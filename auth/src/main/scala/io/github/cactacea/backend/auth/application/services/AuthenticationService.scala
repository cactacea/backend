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
class AuthenticationService @Inject()(
                                       db: DatabaseService,
                                       response: ResponseBuilder,
                                       usersValidator: UsersValidator,
                                       usersRepository: UsersRepository,
                                       authenticationsRepository: AuthenticationsRepository,
                                       authInfoRepository: AuthInfoRepository,
                                       tokensRepository: TokensRepository,
                                       credentialsProvider: CredentialsProvider,
                                       passwordHasherRegistry: PasswordHasherRegistry,
                                       authenticatorService: JWTAuthenticatorService,
                                       mailer: Mailer
                               ) {

  import db._

  def signUp(userName: String, password: String)(implicit request: Request): Future[Response] = {
    val l = LoginInfo(CredentialsProvider.ID, userName)
    transaction {
      for {
        _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
        a <- usersRepository.create(userName)
        _ <- authenticationsRepository.link(l.providerId, l.providerKey, a.id.sessionId)
        _ <- authenticationsRepository.confirm(l.providerId, l.providerKey)
        s <- authenticatorService.create(l)
        c <- authenticatorService.init(s)
        r <- authenticatorService.embed(c, response.ok)
      } yield (r)
    }

  }

  def signIn(userName: String, password: String)(implicit request: Request): Future[Response] = {
    transaction {
      for {
        l <- credentialsProvider.authenticate(Credentials(userName, password))
        s <- authenticatorService.create(l)
        c <- authenticatorService.init(s)
        r <- authenticatorService.embed(c, response.ok)
      } yield (r)
    }
  }

  def changeUserName(userName: String, sessionId: SessionId): Future[Unit] = {
    db.transaction {
      for {
        _ <- authenticationsRepository.updateUserName(CredentialsProvider.ID, userName, sessionId)
      } yield (())
    }
  }

  def changePassword(password: String, sessionId: SessionId): Future[Unit] = {
    for {
      a <- usersValidator.mustFind(sessionId)
      _ <- db.transaction(authInfoRepository.update(LoginInfo(CredentialsProvider.ID, a.userName), passwordHasherRegistry.current.hash(password)))
    } yield (())
  }

  def recoverPassword(email: String, locale: Locale): Future[Unit] = {
    transaction {
      authenticationsRepository.find(EmailsProvider.ID, email).flatMap(_ match {
        case Some(_) =>
          for {
            t <- tokensRepository.issue(EmailsProvider.ID, email, TokenType.resetPassword)
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
        a <- usersRepository.find(l.providerId, l.providerKey)
        _ <- authInfoRepository.update(LoginInfo(CredentialsProvider.ID, a.userName), passwordHasherRegistry.current.hash(password))
      } yield (())
    }
  }

}
