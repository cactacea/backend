/**
 * Copyright 2015 Mohiva Organisation (license at mohiva dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.cactacea.filhouette.impl.authenticators

import java.util.Date

import com.twitter.finagle.http.{Request, Response}
import com.twitter.conversions.DurationOps._
import com.twitter.util._
import io.github.cactacea.filhouette.api.Authenticator.Implicits._
import io.github.cactacea.filhouette.api.crypto.AuthenticatorEncoder
import io.github.cactacea.filhouette.api.exceptions._
import io.github.cactacea.filhouette.api.repositories.AuthenticatorRepository
import io.github.cactacea.filhouette.api.services.AuthenticatorService
import io.github.cactacea.filhouette.api.services.AuthenticatorService._
import io.github.cactacea.filhouette.api.util.IDGenerator
import io.github.cactacea.filhouette.api.util.RequestExtractor._
import io.github.cactacea.filhouette.api.{ExpirableAuthenticator, Logger, LoginInfo, StorableAuthenticator, util}
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticator._
import io.github.cactacea.filhouette.impl.authenticators.JWTAuthenticatorService._
import io.github.cactacea.filhouette.impl.util.Json
import io.jsonwebtoken._
import org.joda.time.DateTime

import scala.collection.JavaConverters._
//import scala.util.{Failure, Success, Try}

/**
 * An authenticator that uses a header based approach with the help of a JWT. It works by
 * using a JWT to transport the authenticator data inside a user defined header. It can
 * be stateless with the disadvantages that the JWT can't be invalidated.
 *
 * The authenticator can use sliding window expiration. This means that the authenticator times
 * out after a certain time if it wasn't used. This can be controlled with the [[idleTimeout]]
 * property. If this feature is activated then a new token will be generated on every update.
 * Make sure your application can handle this case.
 *
 * @see http://self-issued.info/docs/draft-ietf-oauth-json-web-token.html#Claims
 * @see https://developer.atlassian.com/static/connect/docs/concepts/understanding-jwt.html
 *
 * @param id                 The authenticator ID.
 * @param loginInfo          The linked login info for an identity.
 * @param lastUsedDateTime   The last used date/time.
 * @param expirationDateTime The expiration date/time.
 * @param idleTimeout        The duration an authenticator can be idle before it timed out.
 * @param customClaims       Custom claims to embed into the token.
 */
case class JWTAuthenticator(
  id: String,
  loginInfo: LoginInfo,
  lastUsedDateTime: DateTime,
  expirationDateTime: DateTime,
  idleTimeout: Option[Duration],
  customClaims: Option[java.util.Map[String, Object]] = None)
  extends StorableAuthenticator with ExpirableAuthenticator {

  /**
   * The Type of the generated value an authenticator will be serialized to.
   */
  override type Value = String
}

/**
 * The companion object.
 */
object JWTAuthenticator {

  /**
   * Serializes the authenticator.
   *
   * @param authenticator        The authenticator to serialize.
   * @param authenticatorEncoder The authenticator encoder.
   * @param settings             The authenticator settings.
   * @return The serialized authenticator.
   */
  def serialize(
    authenticator: JWTAuthenticator,
    authenticatorEncoder: AuthenticatorEncoder,
    settings: JWTAuthenticatorSettings): String = {

    val signatureAlgorithm = SignatureAlgorithm.HS256
    val subject = Json.toJson(authenticator.loginInfo)
    val issuedAt = new Date() // new Date(authenticator.lastUsedDateTime.getMillis / 1000)
    val expired = new DateTime().plus(authenticator.expirationDateTime.getMillis / 1000).toDate //new Date(authenticator.expirationDateTime.getMillis / 1000)



    authenticator.customClaims.foreach({ c =>
      c.asScala.foreach({ case (key, _) =>
        if (ReservedClaims.contains(key)) {
          Future.exception(new AuthenticatorException(OverrideReservedClaim.format(ID, key, ReservedClaims.mkString(", "))))
        }
      })
    })

    val token = Jwts.builder()
      .setIssuer(settings.issuerClaim)
      .setIssuedAt(issuedAt)
      .setSubject(authenticatorEncoder.encode(subject))
      .setExpiration(expired)
      .setId(authenticator.id)
    authenticator.customClaims.foreach(token.addClaims(_))

    settings.authenticateType +  token
      .signWith(signatureAlgorithm, settings.sharedSecret)
      .compact()

  }

  /**
   * Unserializes the authenticator.
   *
   * @param str                  The string representation of the authenticator.
   * @param authenticatorEncoder The authenticator encoder.
   * @param settings             The authenticator settings.
   * @return An authenticator on success, otherwise a failure.
   */
  def unserialize(
    str: String,
    authenticatorEncoder: AuthenticatorEncoder,
    settings: JWTAuthenticatorSettings): Try[JWTAuthenticator] = {

    Try {

      val token = str.drop(settings.authenticateType.length)

      Jwts.parser()
        .setSigningKey(settings.sharedSecret)
        .parseClaimsJws(token)

    }.map { jwtObject =>

      val body = jwtObject.getBody()
      val loginInfo = Json.fromJson[LoginInfo](authenticatorEncoder.decode(body.getSubject))
      val filteredClaims = body.asScala.filterNot({ case (k, v) => ReservedClaims.contains(k) || v == null })
      val customClaims = filteredClaims.map({ case (k, v) => (k, v.asInstanceOf[Object]) }).asJava

      JWTAuthenticator(
        id = body.getId,
        loginInfo = loginInfo,
        lastUsedDateTime = new DateTime(body.getIssuedAt),
        expirationDateTime = new DateTime(body.getExpiration),
        idleTimeout = settings.authenticatorIdleTimeout,
        customClaims = if (customClaims.isEmpty) None else Some(customClaims)
      )

    }.rescue {
      case e => throw new AuthenticatorException(InvalidJWTToken.format(ID, str), e)
    }

  }

}

/**
 * The service that handles the JWT authenticator.
 *
 * If the authenticator DAO is deactivated then a stateless approach will be used. But note
 * that you will loose the possibility to invalidate a JWT.
 *
 * @param settings             The authenticator settings.
 * @param repository           The repository to persist the authenticator. Set it to None to use a stateless approach.
 * @param authenticatorEncoder The authenticator encoder.
 * @param idGenerator          The ID generator used to create the authenticator ID.
 * @param clock                The clock implementation.
 */
class JWTAuthenticatorService(
  settings: JWTAuthenticatorSettings,
  repository: Option[AuthenticatorRepository[JWTAuthenticator]],
  authenticatorEncoder: AuthenticatorEncoder,
  idGenerator: IDGenerator,
  clock: util.Clock)
  extends AuthenticatorService[JWTAuthenticator]
  with Logger {

  /**
   * Creates a new authenticator for the specified login info.
   *
   * @param loginInfo The login info for which the authenticator should be created.
   * @param request The request header.
   * @return An authenticator.
   */
  override def create(loginInfo: LoginInfo)(implicit request: Request): Future[JWTAuthenticator] = {
    idGenerator.generate.map { id =>
      val now = clock.now
      JWTAuthenticator(
        id = id,
        loginInfo = loginInfo,
        lastUsedDateTime = now,
        expirationDateTime = now + settings.authenticatorExpiry,
        idleTimeout = settings.authenticatorIdleTimeout
      )
    }.rescue {
      case e => Future.exception(new AuthenticatorCreationException(CreateError.format(ID, loginInfo), e))
    }
  }

  /**
   * Retrieves the authenticator from request.
   *
   * If a backing store is defined, then the authenticator will be validated against it.
   *
   * @param request The request to retrieve the authenticator from.
   * @return Some authenticator or None if no authenticator could be found in request.
   */
  override def retrieve(implicit request: Request): Future[Option[JWTAuthenticator]] = {
    request.extractString(settings.fieldName) match {
      case Some(token) => unserialize(token, authenticatorEncoder, settings) match {
        case Return(authenticator) => repository.fold(Future.value(Option(authenticator)))(_.find(authenticator.id))
        case Throw(e) =>
          logger.info(e.getMessage, e)
          Future.None
      }
      case None => Future.None
    }
  }

  /**
   * Creates a new JWT for the given authenticator and return it. If a backing store is defined, then the
   * authenticator will be stored in it.
   *
   * @param authenticator The authenticator instance.
   * @param request       The request header.
   * @return The serialized authenticator value.
   */
  override def init(authenticator: JWTAuthenticator)(implicit request: Request): Future[String] = {
    repository.fold(Future.value(authenticator))(_.add(authenticator)).map { a =>
      serialize(a, authenticatorEncoder, settings)
    }.rescue {
      case e =>
        Future.exception(new AuthenticatorInitializationException(InitError.format(ID, authenticator), e))
    }
  }

  /**
   * Adds a header with the token as value to the result.
   *
   * @param token  The token to embed.
   * @param response The response to manipulate.
   * @return The manipulated result.
   */
  override def embed(token: String, response: Response)(implicit request: Request): Future[Response] = {
    response.headerMap.add(settings.fieldName, token)
    Future.value(response)
  }

  /**
   * Adds a header with the token as value to the request.
   *
   * @param token   The token to embed.
   * @param request The request header.
   * @return The manipulated request header.
   */
  override def embed(token: String, request: Request): Request = {
    request.headerMap.add(settings.fieldName, token)
    request
  }

  /**
   * @inheritdoc
   *
   * @param authenticator The authenticator to touch.
   * @return The touched authenticator on the left or the untouched authenticator on the right.
   */
  override def touch(authenticator: JWTAuthenticator): Either[JWTAuthenticator, JWTAuthenticator] = {
    if (authenticator.idleTimeout.isDefined) {
      Left(authenticator.copy(lastUsedDateTime = clock.now))
    } else {
      Right(authenticator)
    }
  }

  /**
   * Updates the authenticator and embeds a new token in the result.
   *
   * To prevent the creation of a new token on every request, disable the idle timeout setting and this
   * method will not be executed.
   *
   * @param authenticator The authenticator to update.
   * @param result        The result to manipulate.
   * @param request       The request header.
   * @return The original or a manipulated result.
   */
  override def update(authenticator: JWTAuthenticator, result: Response)(
    implicit
    request: Request): Future[Response] = {

    repository.fold(Future.value(authenticator))(_.update(authenticator)).map { a =>
      result.headerMap.add(settings.fieldName, serialize(a, authenticatorEncoder, settings))
      result
    }.rescue {
      case e => Future.exception(new AuthenticatorUpdateException(UpdateError.format(ID, authenticator), e))
    }
  }

  /**
   * Renews an authenticator.
   *
   * After that it isn't possible to use a JWT which was bound to this authenticator. This method
   * doesn't embed the the authenticator into the result. This must be done manually if needed
   * or use the other renew method otherwise.
   *
   * @param authenticator The authenticator to renew.
   * @param request       The request header.
   * @return The serialized expression of the authenticator.
   */
  override def renew(authenticator: JWTAuthenticator)(implicit request: Request): Future[String] = {
    repository.fold(Future.value(()))(_.remove(authenticator.id)).flatMap { _ =>
      create(authenticator.loginInfo).map(_.copy(customClaims = authenticator.customClaims)).flatMap(init)
    }.rescue {
      case e => Future.exception(new AuthenticatorRenewalException(RenewError.format(ID, authenticator), e))
    }
  }

  /**
   * Renews an authenticator and replaces the JWT header with a new one.
   *
   * If a backing store is defined, the old authenticator will be revoked. After that, it isn't
   * possible to use a JWT which was bound to this authenticator.
   *
   * @param authenticator The authenticator to update.
   * @param result        The result to manipulate.
   * @param request       The request header.
   * @return The original or a manipulated result.
   */
  override def renew(authenticator: JWTAuthenticator, result: Response)(
    implicit
    request: Request): Future[Response] = {

    renew(authenticator).flatMap(v => embed(v, result)).rescue {
      case e => Future.exception(new AuthenticatorRenewalException(RenewError.format(ID, authenticator), e))
    }
  }

  /**
   * Removes the authenticator from backing store.
   *
   * @param result  The result to manipulate.
   * @param request The request header.
   * @return The manipulated result.
   */
  override def discard(authenticator: JWTAuthenticator, result: Response)(
    implicit
    request: Request): Future[Response] = {

    repository.fold(Future.value(()))(_.remove(authenticator.id)).map { _ =>
      result
    }.rescue {
      case e => Future.exception(new AuthenticatorDiscardingException(DiscardError.format(ID, authenticator), e))
    }
  }
}

/**
 * The companion object of the authenticator service.
 */
object JWTAuthenticatorService {

  /**
   * The ID of the authenticator.
   */
  val ID = "jwt-authenticator"

  /**
   * The error messages.
   */
  val InvalidJWTToken = "[Filhouette][%s] Error on parsing JWT token: %s"
  val JsonParseError = "[Filhouette][%s] Cannot parse Json: %s"
  val UnexpectedJsonValue = "[Filhouette][%s] Unexpected Json value: %s"
  val OverrideReservedClaim = "[Filhouette][%s] Try to overriding a reserved claim `%s`; list of reserved claims: %s"

  /**
   * The reserved claims used by the authenticator.
   */
  val ReservedClaims = Seq("jti", "iss", "sub", "iat", "exp")
}

/**
 * The settings for the JWT authenticator.
 *
 * @param fieldName                The name of the field in which the token will be transferred in any part
 *                                 of the request.
 * @param issuerClaim              The issuer claim identifies the principal that issued the JWT.
 * @param authenticatorIdleTimeout The duration an authenticator can be idle before it timed out.
 * @param authenticatorExpiry      The duration an authenticator expires after it was created.
 * @param sharedSecret             The shared secret to sign the JWT.
 */
case class JWTAuthenticatorSettings(
  fieldName: String = "Authorization",
  authenticateType: String = "Bearer ",
  issuerClaim: String = "finatra-filhouette",
  authenticatorIdleTimeout: Option[Duration] = None,
  authenticatorExpiry: Duration = 12 hours,
  sharedSecret: String)
