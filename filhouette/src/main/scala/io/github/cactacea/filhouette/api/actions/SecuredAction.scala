/**
  * Original work: SecureSocial (https://github.com/jaliss/securesocial)
  * Copyright 2013 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
  *
  * Derivative work: Silhouette (https://github.com/mohiva/play-silhouette)
  * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
  *
  * Derivative work: Filhouette (https://github.com/cactacea/filhouette)
  * Modifications Copyright 2018 Takeshi Shimada
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
package io.github.cactacea.filhouette.api.actions

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.services.AuthenticatorService
import io.github.cactacea.filhouette.api._
import io.github.cactacea.filhouette.api.services.IdentityService

/**
  * A request that only allows access if an identity is authenticated and authorized.
  *
  * @param identity      The identity implementation.
  * @param authenticator The authenticator implementation.
  * @param request The current request.
  * @tparam I The type of the identity.
  * @tparam A The type of the authenticator.
  */
case class SecuredRequest[I <: Identity, A <: Authenticator](identity: I, authenticator: A, request: Request)

/**
  * Request handler builder implementation to provide the foundation for secured request handlers.
  *
  * @tparam I The type of the identity.
  * @tparam A The type of the authenticator.
  * @param errorHandler  The instance of the secured error handler.
  * @param authorization Maybe an authorization instance.
  */
case class SecuredRequestHandlerBuilder[I <: Identity, A <: Authenticator](
                                                                            identityService: IdentityService[I],
                                                                            authenticatorService: AuthenticatorService[A],
                                                                            requestProviders: Seq[RequestProvider],
                                                                            errorHandler: SecuredErrorHandler,
                                                                            authorization: Option[Authorization[I, A]])
  extends RequestHandlerBuilder[I, A, ({ type R = SecuredRequest[I, A] })#R] {

  /**
    * Creates a secured action handler builder with a new error handler in place.
    *
    * @param errorHandler An error handler instance.
    * @return A secured action handler builder with a new error handler in place.
    */
  def apply(errorHandler: SecuredErrorHandler) =
    SecuredRequestHandlerBuilder[I, A](identityService, authenticatorService, requestProviders, errorHandler, authorization)

  /**
    * Creates a secured action handler builder with an authorization in place.
    *
    * @param authorization An authorization object that checks if the user is authorized to invoke the action.
    * @return A secured action handler builder with an authorization in place.
    */
  def apply(authorization: Authorization[I, A]) =
    SecuredRequestHandlerBuilder[I, A](identityService, authenticatorService, requestProviders, errorHandler, Some(authorization))

  /**
    * Invokes the block.
    *
    * @param block   The block of code to invoke.
    * @param request The current request.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  override def invokeBlock[T](block: SecuredRequest[I, A] => Future[HandlerResult[T]])(
    implicit
    request: Request
  ): Future[HandlerResult[T]] = {
    withAuthorization(handleAuthentication).flatMap {
      // A user is both authenticated and authorized. The request will be granted
      case (Some(authenticator), Some(identity), Some(authorized)) if authorized =>
        handleBlock(authenticator, a => block(SecuredRequest(identity, a, request)))
      // A user is authenticated but not authorized. The request will be forbidden
      case (Some(authenticator), Some(_), _) =>
        handleBlock(authenticator, _ => errorHandler.onNotAuthorized.map(r => HandlerResult(r)))
      // An authenticator but no user was found. The request will ask for authentication and the authenticator will be discarded
      case (Some(authenticator), None, _) =>
        for {
          result <- errorHandler.onNotAuthenticated
          discardedResult <- authenticatorService.discard(authenticator.extract, result)
        } yield HandlerResult(discardedResult)
      // No authenticator and no user was found. The request will ask for authentication
      case _ =>
        errorHandler.onNotAuthenticated.map(r => HandlerResult(r))
    }
  }

  /**
    * Adds the authorization status to the authentication result.
    *
    * @param result  The authentication result.
    * @param request The current request.
    * @return The authentication result with the additional authorization status.
    */
  private def withAuthorization(result: Future[(Option[Either[A, A]], Option[I])])(implicit request: Request) = {
    result.flatMap {
      case (Some(a), Some(i)) =>
        authorization.map(_.isAuthorized(i, a.extract)).getOrElse(Future.True).map(b => (Some(a), Some(i), Some(b)))
      case (a, i) =>
        Future.value((a, i, None))
    }
  }
}

/**
  * A secured request handler.
  *
  * A handler which intercepts requests and checks if there is an authenticated user.
  * If there is one, the execution continues and the enclosed code is invoked.
  *
  * If the user is not authenticated or not authorized, the request is forwarded to
  * the [[io.github.cactacea.filhouette.api.actions.SecuredErrorHandler.onNotAuthenticated]] or
  * the [[io.github.cactacea.filhouette.api.actions.SecuredErrorHandler.onNotAuthorized]] methods.
  */
trait SecuredRequestHandler {

  /**
    * The instance of the secured error handler.
    */
  val errorHandler: SecuredErrorHandler

  /**
    * Applies the environment to the request handler stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @return A secured request handler builder.
    */
  def apply[I <: Identity, A <: Authenticator](
                                                identityService: IdentityService[I],
                                                authenticatorService: AuthenticatorService[A],
                                                requestProviders: Seq[RequestProvider]
                                              ): SecuredRequestHandlerBuilder[I, A]
}


/**
  * Default implementation of the [[SecuredRequestHandler]].
  *
  * @param errorHandler The instance of the secured error handler.
  */
class DefaultSecuredRequestHandler(val errorHandler: SecuredErrorHandler)
  extends SecuredRequestHandler {

  /**
    * Applies the environment to the request handler stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @param identityService      The identity service.
    * @param authenticatorService The authenticator service.
    * @return A secured request handler builder.
    */
  override def apply[I <: Identity, A <: Authenticator](
                                                         identityService: IdentityService[I],
                                                         authenticatorService: AuthenticatorService[A],
                                                         requestProviders: Seq[RequestProvider]
                                                       ) =
    SecuredRequestHandlerBuilder[I, A](identityService, authenticatorService, requestProviders, errorHandler, None)
}

/**
  * Action builder implementation to provide the foundation for secured actions.
  *
  * @param requestHandler The request handler instance.
  * @tparam I The type of the identity.
  * @tparam A The type of the authenticator.
  */
case class SecuredActionBuilder[I <: Identity, A <: Authenticator ](requestHandler: SecuredRequestHandlerBuilder[I, A]) {

  /**
    * Creates a secured action builder with a new error handler in place.
    *
    * @param errorHandler An error handler instance.
    * @return A secured action builder.
    */
  def apply(errorHandler: SecuredErrorHandler) = SecuredActionBuilder[I, A](requestHandler(errorHandler))

  /**
    * Creates a secured action builder with an authorization in place.
    *
    * @param authorization An authorization object that checks if the user is authorized to invoke the action.
    * @return A secured action builder.
    */
  def apply(authorization: Authorization[I, A]) = SecuredActionBuilder[I, A](requestHandler(authorization))

  /**
    * Invokes the block.
    *
    * @param request The current request.
    * @param block   The block of code to invoke.
    * @return A handler result.
    */
  def invokeBlock(request: Request, block: SecuredRequest[I, A] => Future[Response]) = {
    implicit val req = request
    val b = (r: SecuredRequest[I, A]) => block(r).map(r => HandlerResult(r))

    requestHandler(request)(b).map(_.result).rescue(requestHandler.errorHandler.exceptionHandler)
  }

}

/**
  * An action based on the [[SecuredRequestHandler]].
  */
trait SecuredAction {

  /**
    * The instance of the secured request handler.
    */
  val requestHandler: SecuredRequestHandler

  /**
    * Applies the environment to the action stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @param identityService      The identity service.
    * @param authenticatorService The authenticator service.
    * @param requestProviders The request providers.
    * @return A secured action builder.
    */
  def apply[I <: Identity, A <: Authenticator ](
                                                 identityService: IdentityService[I],
                                                 authenticatorService: AuthenticatorService[A],
                                                 requestProviders: Seq[RequestProvider]
                                               ): SecuredActionBuilder[I, A]
}

/**
  * Default implementation of the [[SecuredAction]].
  *
  * @param requestHandler The instance of the secured request handler.
  */
class DefaultSecuredAction(val requestHandler: SecuredRequestHandler) extends SecuredAction {

  /**
    * Applies the environment to the action stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @param identityService      The identity service.
    * @param authenticatorService The authenticator service.
    * @return A secured action builder.
    */
  override def apply[I <: Identity, A <: Authenticator ](
                                                          identityService: IdentityService[I],
                                                          authenticatorService: AuthenticatorService[A],
                                                          requestProviders: Seq[RequestProvider]
                                                        ) =
    SecuredActionBuilder[I, A](requestHandler[I, A](identityService, authenticatorService, requestProviders))
}

/**
  * Error handler for secured actions.
  */
trait SecuredErrorHandler extends NotAuthenticatedErrorHandler with NotAuthorizedErrorHandler {

  /**
    * Exception handler which chains the exceptions handlers from the sub types.
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  override def exceptionHandler(implicit request: Request): PartialFunction[Throwable, Future[Response]] = {
    super[NotAuthenticatedErrorHandler].exceptionHandler orElse
      super[NotAuthorizedErrorHandler].exceptionHandler
  }
}


/**
  * Default implementation of the [[SecuredErrorHandler]].
  *
  */
class DefaultSecuredErrorHandler
  extends SecuredErrorHandler
    with DefaultNotAuthenticatedErrorHandler
    with DefaultNotAuthorizedErrorHandler {

  /**
    * Exception handler which chains the exceptions handlers from the sub types.
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  override def exceptionHandler(implicit request: Request): PartialFunction[Throwable, Future[Response]] = {
    super[DefaultNotAuthenticatedErrorHandler].exceptionHandler orElse
      super[DefaultNotAuthorizedErrorHandler].exceptionHandler
  }
}
