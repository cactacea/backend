package io.github.cactacea.backend.utils.auth

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.DeviceType
import io.github.cactacea.backend.core.domain.repositories.AccountsRepository
import io.github.cactacea.backend.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers.SessionId
import io.github.cactacea.backend.core.infrastructure.validators.AccountsValidator
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.util.{Credentials, PasswordHasherRegistry}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class SessionsService @Inject()(
                                 db: DatabaseService,
                                 response: ResponseBuilder,
                                 accountsValidator: AccountsValidator,
                                 accountsRepository: AccountsRepository,
                                 authInfoRepository: AuthInfoRepository,
                                 authenticationsDAO: AuthenticationsDAO,
                                 credentialsProvider: CredentialsProvider,
                                 passwordHasherRegistry: PasswordHasherRegistry,
                                 authenticatorService: JWTAuthenticatorService,
                                 listenerService: ListenerService
                               ) {

  import db._

  def signUp(accountName: String,
             password: String,
             udid: String,
             userAgent: Option[String],
             deviceType: DeviceType)(implicit request: Request): Future[Response] = {

    val l = LoginInfo(CredentialsProvider.ID, accountName)

    for {
        (r, a) <- transaction {
          for {
            a <- accountsRepository.create(accountName, udid, deviceType, userAgent)
            _ <- authInfoRepository.add(l, passwordHasherRegistry.current.hash(password))
            _ <- authenticationsDAO.updateAccountId(CredentialsProvider.ID, accountName, a.id)
            s <- authenticatorService.create(l)
            c <- authenticatorService.init(s)
            r <- authenticatorService.embed(c, response.ok(a))
          } yield (r, a)
        }
        _ <- listenerService.signedUp(a)
      } yield (r)

  }

  def signIn(accountName: String,
             password: String,
             udid: String,
             userAgent: Option[String],
             deviceType: DeviceType)(implicit request: Request): Future[Response] = {

    transaction {
      for {
        (r, a) <- transaction {
          for {
            l <- credentialsProvider.authenticate(Credentials(accountName, password))
            a <- accountsRepository.find(accountName, udid, deviceType, userAgent)
            s <- authenticatorService.create(l)
            c <- authenticatorService.init(s)
            r <- authenticatorService.embed(c, response.ok(a))
          } yield (r, a)
        }
        _ <- listenerService.signedIn(a)
      } yield (r)

    }
  }

  def updatePassword(password: String, sessionId: SessionId): Future[Unit] = {
    for {
      a <- accountsValidator.find(sessionId)
      _ <- db.transaction(authInfoRepository.update(LoginInfo(CredentialsProvider.ID, a.accountName), passwordHasherRegistry.current.hash(password)))
    } yield (())
  }

  def signOut(udid: String, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- db.transaction(accountsRepository.signOut(udid, sessionId))
      _ <- listenerService.signedOut(sessionId)
    } yield (())
  }

}
