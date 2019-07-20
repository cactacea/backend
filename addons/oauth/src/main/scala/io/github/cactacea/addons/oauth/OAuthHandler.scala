package io.github.cactacea.addons.oauth

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.oauth2.{AccessToken, AuthInfo, DataHandler}
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.{AccountsDAO, AuthDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

@Singleton
class OAuthHandler @Inject()(
                              accountsDAO: AccountsDAO,
                              authDAO: AuthDAO,
                              codeGenerator: OAuthCodeGenerator,
                              tokenGenerator: OAuthTokenGenerator
                            ) extends DataHandler[OAuthUser] with Logging {

  def validateClient(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    authDAO.validateClient(clientId, clientSecret, grantType)
  }

  def findUser(username: String, password: String): Future[Option[OAuthUser]] = {
    accountsDAO.find(username, password)
      .map(_.map({ a => OAuthUser(a.id, 0, new Date(), 0L) }))
  }

  def findClientUser(clientId: String, clientSecret: String, scope: Option[String]): Future[Option[OAuthUser]] = {
    Future.None
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[OAuthUser]]] = {
    codeGenerator.parseCode(code) match {
      case Some(t) =>
        val p = Permissions.forScope(t.scope).map(_.key).fold(0){ _ + _ }
        authDAO.findClient(t.clientId).map(_.map(c =>
          AuthInfo(OAuthUser(AccountId(t.audience), p, t.issuedAt, t.expiration), t.clientId, t.scope, Some(c.redirectUri))
        ))
      case None =>
        Future.None
    }
  }

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[OAuthUser]]] = {
    tokenGenerator.parseRefreshToken(refreshToken) match {
      case Some(t) =>
        val p = Permissions.forScope(t.scope).map(_.key).fold(0){ _ + _ }
        val a = Some(AuthInfo(OAuthUser(AccountId(t.audience), p, t.issuedAt, t.expiration), t.clientId, t.scope, None))
        Future.value(a)
      case None =>
        Future.None
    }
  }

  def createAccessToken(authInfo: AuthInfo[OAuthUser]): Future[AccessToken] = {
    val token = tokenGenerator.generateToken(authInfo.user.accountId.value, authInfo.clientId, authInfo.scope.getOrElse(""))
    val refreshToken = tokenGenerator.generateRefreshToken(authInfo.user.accountId.value, authInfo.clientId, authInfo.scope.getOrElse(""))
    Future.value(AccessToken(token, Some(refreshToken), None, None, new Date()))
  }

  def refreshAccessToken(authInfo: AuthInfo[OAuthUser], refreshToken: String): Future[AccessToken] = {
    val token = tokenGenerator.generateToken(authInfo.user.accountId.value, authInfo.clientId, authInfo.scope.getOrElse(""))
    Future.value(AccessToken(token, None, None, None, new Date()))
  }

  def findAccessToken(token: String): Future[Option[AccessToken]] = {
    tokenGenerator.parseToken(token) match {
      case Some(p) =>
        val a = Some(AccessToken(token, None, p.scope, Some(p.expiration), p.issuedAt))
        Future.value(a)
      case None =>
        Future.None
    }
  }

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[OAuthUser]]] = {
    tokenGenerator.parseToken(accessToken.token) match {
      case Some(t) =>
        val p = Permissions.forScope(t.scope).map(_.key).fold(0){ _ + _ }
        val a = Some(AuthInfo(OAuthUser(AccountId(t.audience), p, t.issuedAt, t.expiration), t.clientId, t.scope, None))
        Future.value(a)
      case None =>
        Future.None
    }
  }

  def getStoredAccessToken(authInfo: AuthInfo[OAuthUser]): Future[Option[AccessToken]] = {
    Future.None
  }

}
