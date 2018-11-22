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

import org.joda.time.DateTime
import com.twitter.finagle.http.{Request, Response}
import com.twitter.conversions.time._
import com.twitter.util.{Duration, Future}
import io.github.cactacea.filhouette.api.Authenticator.Implicits._
import io.github.cactacea.filhouette.api.exceptions._
import io.github.cactacea.filhouette.api.repositories.AuthenticatorRepository
import io.github.cactacea.filhouette.api.services.AuthenticatorService
import io.github.cactacea.filhouette.api.services.AuthenticatorService._
import io.github.cactacea.filhouette.api.util.RequestExtractor._
import io.github.cactacea.filhouette.api.util.{Clock, IDGenerator}
import io.github.cactacea.filhouette.api.{ExpirableAuthenticator, Logger, LoginInfo, StorableAuthenticator}
import io.github.cactacea.filhouette.impl.authenticators.BearerTokenAuthenticatorService._

/**
 * An authenticator that uses a header based approach with the help of a bearer token. It
 * works by transporting a token in a user defined header to track the authenticated user
 * and a server side backing store that maps the token to an authenticator instance.
 *
 * The authenticator can use sliding window expiration. This means that the authenticator times
 * out after a certain time if it wasn't used. This can be controlled with the [[idleTimeout]]
 * property.
 *
 * Note: If deploying to multiple nodes the backing store will need to synchronize.
 *
 * @param id                 The authenticator ID.
 * @param loginInfo          The linked login info for an identity.
 * @param lastUsedDateTime   The last used date/time.
 * @param expirationDateTime The expiration date/time.
 * @param idleTimeout        The duration an authenticator can be idle before it timed out.
 */
case class BearerTokenAuthenticator(
  id: String,
  loginInfo: LoginInfo,
  lastUsedDateTime: DateTime,
  expirationDateTime: DateTime,
  idleTimeout: Option[Duration])
  extends StorableAuthenticator with ExpirableAuthenticator {

  /**
   * The Type of the generated value an authenticator will be serialized to.
   */
  override type Value = String
}

/**
 * The service that handles the bearer token authenticator.
 *
 * @param settings         The authenticator settings.
 * @param repository       The repository to persist the authenticator.
 * @param idGenerator      The ID generator used to create the authenticator ID.
 * @param clock            The clock implementation.
 */
class BearerTokenAuthenticatorService(
  settings: BearerTokenAuthenticatorSettings,
  repository: AuthenticatorRepository[BearerTokenAuthenticator],
  idGenerator: IDGenerator,
  clock: Clock)
  extends AuthenticatorService[BearerTokenAuthenticator]
  with Logger {

  /**
   * Creates a new authenticator for the specified login info.
   *
   * @param loginInfo The login info for which the authenticator should be created.
   * @param request   The request header.
   * @return An authenticator.
   */
  override def create(loginInfo: LoginInfo)(implicit request: Request): Future[BearerTokenAuthenticator] = {
    idGenerator.generate.map { id =>
      val now = clock.now
      BearerTokenAuthenticator(
        id = id,
        loginInfo = loginInfo,
        lastUsedDateTime = now,
        expirationDateTime = now + settings.authenticatorExpiry,
        idleTimeout = settings.authenticatorIdleTimeout)
    }.rescue {
      case e => Future.exception(new AuthenticatorCreationException(CreateError.format(ID, loginInfo), e))
    }
  }

  /**
   * Retrieves the authenticator from request.
   *
   * @param request The request to retrieve the authenticator from.
   * @return Some authenticator or None if no authenticator could be found in request.
   */
  override def retrieve(implicit request: Request): Future[Option[BearerTokenAuthenticator]] = {
    request.extractString(settings.fieldName) match {
      case Some(token) => repository.find(token)
      case None        => Future.None
    }
  }

  /**
   * Creates a new bearer token for the given authenticator and return it. The authenticator will also be
   * stored in the backing store.
   *
   * @param authenticator The authenticator instance.
   * @param request       The request header.
   * @return The serialized authenticator value.
   */
  override def init(authenticator: BearerTokenAuthenticator)(implicit request: Request): Future[String] = {
    repository.add(authenticator).map { a =>
      a.id
    }.rescue {
      case e => Future.exception(new AuthenticatorInitializationException(InitError.format(ID, authenticator), e))
    }
  }

  /**
   * Adds a header with the token as value to the result.
   *
   * @param token   The token to embed.
   * @param result  The result to manipulate.
   * @param request The request header.
   * @return The manipulated result.
   */
  override def embed(token: String, result: Response)(implicit request: Request): Future[Response] = {
    result.headerMap.add(settings.fieldName, token)
    Future.value(result)
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
  override def touch(authenticator: BearerTokenAuthenticator): Either[BearerTokenAuthenticator, BearerTokenAuthenticator] = {
    if (authenticator.idleTimeout.isDefined) {
      Left(authenticator.copy(lastUsedDateTime = clock.now))
    } else {
      Right(authenticator)
    }
  }

  /**
   * Updates the authenticator with the new last used date in the backing store.
   *
   * We needn't embed the token in the response here because the token itself will not be changed.
   * Only the authenticator in the backing store will be changed.
   *
   * @param authenticator The authenticator to update.
   * @param result        The result to manipulate.
   * @param request       The request header.
   * @return The original or a manipulated result.
   */
  override def update(authenticator: BearerTokenAuthenticator, result: Response)(
    implicit
    request: Request): Future[Response] = {

    repository.update(authenticator).map { _ =>
      result
    }.rescue {
      case e => Future.exception(new AuthenticatorUpdateException(UpdateError.format(ID, authenticator), e))
    }
  }

  /**
   * Renews an authenticator.
   *
   * After that it isn't possible to use a bearer token which was bound to this authenticator. This
   * method doesn't embed the the authenticator into the result. This must be done manually if needed
   * or use the other renew method otherwise.
   *
   * @param authenticator The authenticator to renew.
   * @param request       The request header.
   * @return The serialized expression of the authenticator.
   */
  override def renew(authenticator: BearerTokenAuthenticator)(
    implicit
    request: Request): Future[String] = {

    repository.remove(authenticator.id).flatMap { _ =>
      create(authenticator.loginInfo).flatMap(init)
    }.rescue {
      case e => Future.exception(new AuthenticatorRenewalException(RenewError.format(ID, authenticator), e))
    }
  }

  /**
   * Renews an authenticator and replaces the bearer token header with a new one.
   *
   * The old authenticator will be revoked. After that it isn't possible to use a bearer token which was
   * bound to this authenticator.
   *
   * @param authenticator The authenticator to update.
   * @param result        The result to manipulate.
   * @param request       The request header.
   * @return The original or a manipulated result.
   */
  override def renew(authenticator: BearerTokenAuthenticator, result: Response)(
    implicit
    request: Request): Future[Response] = {

    renew(authenticator).flatMap(v => embed(v, result)).rescue {
      case e => Future.exception(new AuthenticatorRenewalException(RenewError.format(ID, authenticator), e))
    }
  }

  /**
   * Removes the authenticator from cache.
   *
   * @param result  The result to manipulate.
   * @param request The request header.
   * @return The manipulated result.
   */
  override def discard(authenticator: BearerTokenAuthenticator, result: Response)(
    implicit
    request: Request): Future[Response] = {

    repository.remove(authenticator.id).map { _ =>
      result
    }.rescue {
      case e => Future.exception(new AuthenticatorDiscardingException(DiscardError.format(ID, authenticator), e))
    }
  }
}

/**
 * The companion object of the authenticator service.
 */
object BearerTokenAuthenticatorService {

  /**
   * The ID of the authenticator.
   */
  val ID = "bearer-token-authenticator"
}

/**
 * The settings for the bearer token authenticator.
 *
 * @param fieldName                The name of the field in which the token will be transferred in any part
 *                                 of the request.
// * @param requestParts             Some request parts from which a value can be extracted or None to extract
// *                                 values from any part of the request.
 * @param authenticatorIdleTimeout The duration an authenticator can be idle before it timed out.
 * @param authenticatorExpiry      The duration an authenticator expires after it was created.
 */
case class BearerTokenAuthenticatorSettings(
  fieldName: String = "X-Auth-Token",
  authenticatorIdleTimeout: Option[Duration] = None,
  authenticatorExpiry: Duration = 12 hours)
