package io.github.cactacea.backend.auth.core.application.services

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.domain.models.Session
import io.github.cactacea.backend.auth.core.domain.repositories.AuthenticationsRepository
import io.github.cactacea.backend.auth.core.infrastructure.validators.AuthenticationsValidator
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.domain.repositories.UsersRepository
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.{Credentials, PasswordHasherRegistry}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class AuthenticationService @Inject()(
                                       db: DatabaseService,
                                       response: ResponseBuilder,
                                       authenticationsValidator: AuthenticationsValidator,
                                       authenticationsRepository: AuthenticationsRepository,
                                       authInfoRepository: AuthInfoRepository,
                                       credentialsProvider: CredentialsProvider,
                                       passwordHasherRegistry: PasswordHasherRegistry,
                                       usersRepository: UsersRepository,
                                       authenticatorService: JWTAuthenticatorService
                               ) {

  import db._

  def signUp(userName: String, password: String)(implicit request: Request): Future[Response] = {
    val l = LoginInfo(CredentialsProvider.ID, userName)
    transaction {
      for {
        _ <- authenticationsValidator.mustNotExists(l.providerId, l.providerKey)
        _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
        _ <- authenticationsRepository.confirm(l)
        u <- usersRepository.create(userName)
        _ <- authenticationsRepository.link(l.providerId, l.providerKey, u)
        s <- authenticatorService.create(l)
        c <- authenticatorService.init(s)
        r <- authenticatorService.embed(c, response.ok.body(Session(userName, c)))
      } yield (r)
    }

  }

  def signIn(userName: String, password: String)(implicit request: Request): Future[Response] = {
    for {
      l <- credentialsProvider.authenticate(Credentials(userName, password))
      s <- authenticatorService.create(l)
      c <- authenticatorService.init(s)
      r <- authenticatorService.embed(c, response.ok.body(Session(userName, c)))
    } yield (r)
  }

  def changeUserName(providerId: String, providerKey: String, newUserName: String): Future[Unit] = {
    db.transaction {
      for {
        _ <- authenticationsRepository.updateUserName(providerId, providerKey, newUserName)
      } yield (())
    }
  }

  def create(providerId: String, providerKey: String, userName: String, displayName: Option[String]): Future[User] = {
    transaction {
      for {
        i <- usersRepository.create(userName, displayName)
        _ <- authenticationsRepository.link(providerId, providerKey, i)
        u <- usersRepository.find(i.sessionId)
      } yield (u)
    }
  }

}


