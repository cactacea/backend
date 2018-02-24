package io.github.cactacea.core.util.oauth

import com.google.inject.Inject
import com.twitter.finagle.oauth2.{AccessToken, AuthInfo, DataHandler}
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.dao.{AccountsDAO, AuthDAO}
import io.github.cactacea.core.util.tokens.OAuthTokenGenerator
import org.joda.time.DateTime

/**
  * Provide accessing to data storage for using OAuth 2.0.
  *
  * <h3>[Authorization phases]</h3>
  *
  * <h4>Authorization Code Grant</h4>
  * <ul>
  *   <li>validateClient(clientId, clientSecret, grantType)</li>
  *   <li>findAuthInfoByCode(code)</li>
  *   <li>getStoredAccessToken(authInfo)</li>
  *   <li>isAccessTokenExpired(token)</li>
  *   <li>refreshAccessToken(authInfo, token)
  *   <li>createAccessToken(authInfo)</li>
  * </ul>
  *
  * <h4>Refresh Token Grant</h4>
  * <ul>
  *   <li>validateClient(clientId, clientSecret, grantType)</li>
  *   <li>findAuthInfoByRefreshToken(refreshToken)</li>
  *   <li>refreshAccessToken(authInfo, refreshToken)</li>
  * </ul>
  *
  * <h4>Resource Owner Password Credentials Grant</h4>
  * <ul>
  *   <li>validateClient(clientId, clientSecret, grantType)</li>
  *   <li>findUser(username, password)</li>
  *   <li>getStoredAccessToken(authInfo)</li>
  *   <li>isAccessTokenExpired(token)</li>
  *   <li>refreshAccessToken(authInfo, token)
  *   <li>createAccessToken(authInfo)</li>
  * </ul>
  *
  * <h4>Client Credentials Grant</h4>
  * <ul>
  *   <li>validateClient(clientId, clientSecret, grantType)</li>
  *   <li>findClientUser(clientId, clientSecret)</li>
  *   <li>getStoredAccessToken(authInfo)</li>
  *   <li>isAccessTokenExpired(token)</li>
  *   <li>refreshAccessToken(authInfo, token)
  *   <li>createAccessToken(authInfo)</li>
  * </ul>
  *
  * <h3>[Access to Protected Resource phase]</h3>
  * <ul>
  *   <li>findAccessToken(token)</li>
  *   <li>isAccessTokenExpired(token)</li>
  *   <li>findAuthInfoByAccessToken(token)</li>
  * </ul>
  */

class OAuthHandler extends DataHandler[OAuthUser] with Logging {

  @Inject var accountsDAO: AccountsDAO = _
  @Inject var authDAO: AuthDAO = _

  def validateClient(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    authDAO.validateClient(clientId, clientSecret, grantType)
  }

  def findUser(username: String, password: String): Future[Option[OAuthUser]] = {
    accountsDAO.find(username, password).map(_.map({ a => OAuthUser(a.id, a.signedOutAt.getOrElse(0L)) }))
  }

  def findClientUser(clientId: String, clientSecret: String, scope: Option[String]): Future[Option[OAuthUser]] = {
    Future.None
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[OAuthUser]]] = {
    OAuthTokenGenerator.parse(code).flatMap({ _ =>
      authDAO.findByCode(code).flatMap({ auth =>
        authDAO.deleteByCode(code).map({ _ =>
          auth.map(auth => AuthInfo(OAuthUser(auth.accountId, auth.expiresIn), auth.clientId, auth.scope, auth.redirectUri))
        })
      })
    })
  }

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[OAuthUser]]] = {
    OAuthTokenGenerator.parse(refreshToken).flatMap({ _ =>
      authDAO.findByRefreshToken(refreshToken).flatMap({ auth =>
        authDAO.deleteByRefreshToken(refreshToken).map({ _ =>
          auth.map(auth => AuthInfo(OAuthUser(auth.accountId, auth.expiresIn), auth.clientId, auth.scope, None))
        })
      })
    })
  }

  def createAccessToken(authInfo: AuthInfo[OAuthUser]): Future[AccessToken] = {
    val accessToken = OAuthTokenGenerator.generateAccessToken()
    val refreshToken = OAuthTokenGenerator.generateRefreshToken()
    authDAO.createToken(accessToken, Some(refreshToken), authInfo.user.accountId, authInfo.clientId, authInfo.scope, 60 * 15, new DateTime().toDate).map({ _ =>
      AccessToken(accessToken, Some(refreshToken), None, None, DateTime.now().toDate)
    })
  }

  def refreshAccessToken(authInfo: AuthInfo[OAuthUser], refreshToken: String): Future[AccessToken] = {
    val accessToken = OAuthTokenGenerator.generateAccessToken()
    authDAO.createToken(accessToken, None, authInfo.user.accountId, authInfo.clientId, authInfo.scope, 60 * 15, new DateTime().toDate).map({ _ =>
      AccessToken(accessToken, None, None, None, DateTime.now().toDate)
    })
  }

  def findAccessToken(token: String): Future[Option[AccessToken]] = {
    authDAO.findByToken(token).map({
      _.map({t => AccessToken(token, None, t.scope, None, t.createdAt)})
    })
  }

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[OAuthUser]]] = {
    authDAO.findByToken(accessToken.token).map({
      _.map({ t => AuthInfo(OAuthUser(t.accountId, t.expiresIn), t.clientId, t.scope, None) })
    })
  }

  def getStoredAccessToken(authInfo: AuthInfo[OAuthUser]): Future[Option[AccessToken]] = {
    Future.None
  }




}