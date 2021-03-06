package io.github.cactacea.backend.oauth

import java.util.Date

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.oauth2.{AccessToken, AuthInfo, DataHandler, InvalidScope}
import com.twitter.util.Future
import io.github.cactacea.backend.auth.core.infrastructure.dao.AuthenticationsDAO
import io.github.cactacea.backend.auth.enums.OAuthTokenType
import io.github.cactacea.backend.core.infrastructure.dao.ClientsDAO
import io.github.cactacea.filhouette.api.util.Credentials
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider

@Singleton
class OAuthDataHandler @Inject()(
                                  authenticationsDAO: AuthenticationsDAO,
                                 clientsDAO: ClientsDAO,
                                 credentialsProvider: CredentialsProvider
                            ) extends DataHandler[OAuthUser] {


  // Authorization Code Flow
  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[OAuthUser]]] = {
    val token = for {
      t <- OAuthTokenGenerator.parse(OAuthTokenType.code, code)
      i <- createAuthInfo(t.userName, t.clientId, t.redirectUri, t.issuedAt, t.scope)
    } yield (i)
    Future.value(token)
  }


  // Refresh Token Flow
  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[OAuthUser]]] = {
    val token = for {
      t <- OAuthTokenGenerator.parse(OAuthTokenType.refreshToken, refreshToken)
      i <- createAuthInfo(t.userName, t.clientId, t.redirectUri, t.issuedAt, t.scope)
    } yield (i)
    Future.value(token)
  }

  // Client Credential Flow
  override def findClientUser(clientId: String, clientSecret: String, scope: Option[String]): Future[Option[OAuthUser]] = {
    for {
      c <- clientsDAO.find(clientId, clientSecret)
      _ <- validateScope(scope, c.flatMap(_.scope))
    } yield (Option(OAuthUser("", new Date())))
  }


  // Password Credentials Grant
  override def findUser(username: String, password: String): Future[Option[OAuthUser]] = {
    for {
      l <- credentialsProvider.authenticate(Credentials(username, password))
      a <- authenticationsDAO.find(l.providerId, l.providerKey).map(_.map(a => OAuthUser(username, new Date())))
    } yield (a)
  }


  // Issue Access Token
  override def validateClient(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    clientsDAO.exists(clientId, clientSecret, grantType)
  }

  // Issue Access Token
  override def createAccessToken(authInfo: AuthInfo[OAuthUser]): Future[AccessToken] = {
    val tokenExpiresIn = 60L * 60
    val refreshExpiresIn = 60L * 60 * 24 * 365
    val token = OAuthTokenGenerator.generate(
      OAuthTokenType.token,
      authInfo.user.userName,
      authInfo.clientId,
      authInfo.scope,
      authInfo.redirectUri,
      tokenExpiresIn)
    val refreshToken = OAuthTokenGenerator.generate(
      OAuthTokenType.refreshToken,
      authInfo.user.userName,
      authInfo.clientId,
      authInfo.scope,
      authInfo.redirectUri,
      refreshExpiresIn)
    val accessToken = AccessToken(token, Option(refreshToken), authInfo.scope, Option(tokenExpiresIn), new Date())
    Future.value(accessToken)
  }

  // Issue Access Token
  override def getStoredAccessToken(authInfo: AuthInfo[OAuthUser]): Future[Option[AccessToken]] = {
    // if return none, createAccessToken will be called.
    Future.None
  }

  // Issue Access Token by Refresh Token
  override def refreshAccessToken(authInfo: AuthInfo[OAuthUser], refreshToken: String): Future[AccessToken] = {
    val tokenExpiresIn = 60L * 60
    val token = OAuthTokenGenerator.generate(
      OAuthTokenType.token,
      authInfo.user.userName,
      authInfo.clientId,
      authInfo.scope,
      authInfo.redirectUri,
      tokenExpiresIn)
    val accessToken = AccessToken(token, Option(refreshToken), authInfo.scope, Option(tokenExpiresIn), new Date())
    Future.value(accessToken)
  }

  // Authorize flow #1
  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    val accessToken = OAuthTokenGenerator.parse(OAuthTokenType.token, token)
      .map(t => AccessToken(token, None, t.scope, t.expiresIn, t.issuedAt))
    Future.value(accessToken)
  }

  // Authorize flow #2
  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[OAuthUser]]] = {
    val authInfo = OAuthTokenGenerator.parse(OAuthTokenType.token, accessToken.token)
      .flatMap(t => createAuthInfo(t.userName, t.clientId, t.redirectUri, t.issuedAt, t.scope))
    Future.value(authInfo)
  }

  // OK
  private def createAuthInfo(userName: String,
                             clientId: String,
                             redirectUri: Option[String],
                             issuedAt: Date,
                             scope: Option[String]): Option[AuthInfo[OAuthUser]] = {

    Option(AuthInfo(OAuthUser(userName, issuedAt), clientId, scope, redirectUri))
  }

  // OK
  private def validateScope(scope: Option[String], validScope: Option[String]): Future[Unit] = {
    (scope, validScope) match {
      case (Some(scope), Some(validScope)) => {
        val scopes = scope.split(' ')
        val validScopes = validScope.split(' ')
        if (scopes.filterNot(validScopes.contains(_)).lengthCompare(0) == 0) {
          Future.exception(new InvalidScope("Invalid scope error occurred."))
        } else {
          Future.Unit
        }
      }
      case _ => Future.Unit
    }
  }

  //
  def createAccessCode(clientId: String, scope: Option[String], redirectUri: Option[String]): Future[String] = {
    val tokenExpiresIn = 60L * 60
    val token = OAuthTokenGenerator.generate(
      OAuthTokenType.code,
      "",
      clientId,
      scope,
      redirectUri,
      tokenExpiresIn)
    Future.value(token)
  }

}
