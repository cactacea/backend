package io.github.cactacea.filhouette.api.services

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.{Authenticator, LoginInfo}

/**
  * A marker result which indicates that an operation on an authenticator was processed and
  * therefore it shouldn't updated automatically.
  *
  * Due the fact that the update method gets called on every subsequent request to update the
  * authenticator related data in the backing store and in the result, it isn't possible to
  * discard or renew the authenticator simultaneously. This is because the "update" method would
  * override the result created by the "renew" or "discard" method, because it will be executed
  * as last in the chain.
  *
  * As example:
  * If we discard the session in a Filhouette action then it will be removed from session. But
  * at the end the update method will embed the session again, because it gets called with the
  * result of the action.
  *
  */

/**
  * Handles authenticators for the Filhouette module.
  *
  * @tparam T The type of the authenticator this service is responsible for.
  */
trait AuthenticatorService[T <: Authenticator] {

  /**
    * Creates a new authenticator for the specified login info.
    *
    * @param loginInfo The login info for which the authenticator should be created.
    * @param request The request header.
    * @return An authenticator.
    */
  def create(loginInfo: LoginInfo)(implicit request: Request): Future[T]

  /**
    * Retrieves the authenticator from request.
    *
    * @param request The request to retrieve the authenticator from.
    * @return Some authenticator or None if no authenticator could be found in request.
    */
  def retrieve(implicit request: Request): Future[Option[T]]

  /**
    * Initializes an authenticator and instead of embedding into the the request or result, it returns
    * the serialized value.
    *
    * @param authenticator The authenticator instance.
    * @param request The request header.
    * @return The serialized authenticator value.
    */
  def init(authenticator: T)(implicit request: Request): Future[T#Value]

  /**
    * Embeds authenticator specific artifacts into the response.
    *
    * @param value The authenticator value to embed.
    * @param result The result to manipulate.
    * @param request The request header.
    * @return The manipulated result.
    */
  def embed(value: T#Value, result: Response)(implicit request: Request): Future[Response]

  /**
    * Embeds authenticator specific artifacts into the request.
    *
    * This method can be used to embed an authenticator in a existing request. This can be useful
    * in Play filters. So before executing a SecuredAction we can embed the authenticator in
    * the request to lead the action to believe that the request is a new request which contains
    * a valid authenticator.
    *
    * If an existing authenticator exists, then it will be overridden.
    *
    * @param value The authenticator value to embed.
    * @param request The request header.
    * @return The manipulated request header.
    */
  def embed(value: T#Value, request: Request): Request

  /**
    * Touches an authenticator.
    *
    * An authenticator can use sliding window expiration. This means that the authenticator times
    * out after a certain time if it wasn't used. So to mark an authenticator as used it will be
    * touched on every request to a Filhouette action. If an authenticator should not be touched
    * because of the fact that sliding window expiration is disabled, then it should be returned
    * on the right, otherwise it should be returned on the left. An untouched authenticator needn't
    * be updated later by the [[update]] method.
    *
    * @param authenticator The authenticator to touch.
    * @return The touched authenticator on the left or the untouched authenticator on the right.
    */
  def touch(authenticator: T): Either[T, T]

  /**
    * Updates a touched authenticator.
    *
    * If the authenticator was updated, then the updated artifacts should be embedded into the response.
    * This method gets called on every subsequent request if an identity accesses a Filhouette action,
    * expect the authenticator was not touched.
    *
    * @param authenticator The authenticator to update.
    * @param result The result to manipulate.
    * @param request The request header.
    * @return The original or a manipulated result.
    */
  def update(authenticator: T, result: Response)(implicit request: Request): Future[Response]

  /**
    * Renews the expiration of an authenticator without embedding it into the result.
    *
    * Based on the implementation, the renew method should revoke the given authenticator first, before
    * creating a new one. If the authenticator was updated, then the updated artifacts should be returned.
    *
    * @param authenticator The authenticator to renew.
    * @param request The request header.
    * @return The serialized expression of the authenticator.
    */
  def renew(authenticator: T)(implicit request: Request): Future[T#Value]

  /**
    * Renews the expiration of an authenticator.
    *
    * Based on the implementation, the renew method should revoke the given authenticator first, before
    * creating a new one. If the authenticator was updated, then the updated artifacts should be embedded
    * into the response.
    *
    * @param authenticator The authenticator to renew.
    * @param result The result to manipulate.
    * @param request The request header.
    * @return The original or a manipulated result.
    */
  def renew(authenticator: T, result: Response)(implicit request: Request): Future[Response]

  /**
    * Manipulates the response and removes authenticator specific artifacts before sending it to the client.
    *
    * @param authenticator The authenticator instance.
    * @param result The result to manipulate.
    * @param request The request header.
    * @return The manipulated result.
    */
  def discard(authenticator: T, result: Response)(implicit request: Request): Future[Response]
}

/**
  * The companion object.
  */
object AuthenticatorService {

  /**
    * The error messages.
    */
  val CreateError = "[Filhouette][%s] Could not create authenticator for login info: %s"
  val RetrieveError = "[Filhouette][%s] Could not retrieve authenticator"
  val InitError = "[Filhouette][%s] Could not init authenticator: %s"
  val UpdateError = "[Filhouette][%s] Could not update authenticator: %s"
  val RenewError = "[Filhouette][%s] Could not renew authenticator: %s"
  val DiscardError = "[Filhouette][%s] Could not discard authenticator: %s"
}
