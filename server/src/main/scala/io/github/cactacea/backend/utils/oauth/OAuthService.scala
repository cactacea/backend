package io.github.cactacea.backend.utils.oauth

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.oauth2._
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, AuthDAO}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Clients}

@Singleton
class OAuthService @Inject()(
                              authDAO: AuthDAO,
                              accountsDAO: AccountsDAO
                            ) {

  def validateClient(clientId: String): Future[Either[OAuthError, (Clients, List[Permission])]] = {
    authDAO.findClient(clientId).map(_ match {
      case Some(c) =>
        val p = Permissions.forScope(c.scope)
        Right((c, p))
      case None =>
        Left((new InvalidClient("Invalid client error occurred.")))
    })

  }

  def validateResponseType(responseType: String): Either[OAuthError, Unit] = {
    responseType match {
      case "code" =>
        Right(Unit)
      case "token" =>
        Right(Unit)
      case _ =>
        Left((new UnsupportedResponseType("Invalid response type error occurred.")))
    }
  }

  def validateScope(scope: Option[String], permissions: List[Permission]): Either[OAuthError, List[Permission]] = {
    val p = Permissions.forScope(scope)
    if (scope.isDefined) {
      if (p.size == permissions.size) {
        Right(p)
      } else {
        Left((new InvalidScope("Invalid scope error occurred.")))
      }
    } else {
      Right(p)
    }
  }

  def signIn(username: String, password: String): Future[Option[Accounts]] = {
    accountsDAO.find(username, password)
  }

}