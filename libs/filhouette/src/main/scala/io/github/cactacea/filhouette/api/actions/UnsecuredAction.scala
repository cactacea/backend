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
  * http://www.apache.org/licenses/LICENSE-2.0
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
import io.github.cactacea.filhouette.api.{Identity, _}
import io.github.cactacea.filhouette.api.services.{AuthenticatorService, IdentityService}

/**
  * Request handler builder implementation to provide the foundation for unsecured request handlers.
  *
  * @tparam I The type of the identity.
  * @tparam A The type of the authenticator.
  * @param errorHandler  The instance of the unsecured error handler.
  */
case class UnsecuredRequestHandlerBuilder[I <: Identity, A <: Authenticator](
                                                                            identityService: IdentityService[I],
                                                                            authenticatorService: AuthenticatorService[A],
                                                                            requestProviders: Seq[RequestProvider],
                                                                            errorHandler: UnsecuredErrorHandler)
  extends RequestHandlerBuilder[I, A, Request] {

  /**
    * Creates an unsecured action handler builder with a new error handler in place.
    *
    * @param errorHandler An error handler instance.
    * @return An unsecured action handler builder with a new error handler in place.
    */
  def apply(errorHandler: UnsecuredErrorHandler): UnsecuredRequestHandlerBuilder[I, A] = {
    UnsecuredRequestHandlerBuilder[I, A](identityService, authenticatorService, requestProviders, errorHandler)
  }

  /**
    * Invokes the block.
    *
    * @param block The block of code to invoke.
    * @param request The current request.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  override def invokeBlock[T](block: Request => Future[HandlerResult[T]])(
    implicit request: Request): Future[HandlerResult[T]] = {
    handleAuthentication.flatMap {
      // A user is authenticated. The request will be forbidden
      case (Some(authenticator), Some(_)) =>
        handleBlock(authenticator, _ => errorHandler.onNotAuthorized.map(r => HandlerResult(r)))
      // An authenticator but no user was found. The request will be granted and the authenticator will be discarded
      case (Some(authenticator), None) => {
        block(request).flatMap {
          case hr @ HandlerResult(pr, _) =>
            authenticatorService.discard(authenticator.extract, pr).map(r => hr.copy(r))
        }
      }
      // No authenticator and no user was found. The request will be granted
      case _ => block(request)
    }
  }


}

/**
  * An unsecured request handler.
  *
  * A handler which intercepts requests and checks if there is no authenticated user.
  * If there is none, the execution continues and the enclosed code is invoked.
  *
  * If the user is authenticated, the request is forwarded to
  * the [[io.github.cactacea.filhouette.api.actions.UnsecuredErrorHandler.onNotAuthorized]] method.
  */
trait UnsecuredRequestHandler {

  /**
    * The instance of the unsecured error handler.
    */
  val errorHandler: UnsecuredErrorHandler

  /**
    * Applies the environment to the request handler stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @return An unsecured request handler builder.
    */
  def apply[I <: Identity, A <: Authenticator](
                                                identityService: IdentityService[I],
                                                authenticatorService: AuthenticatorService[A],
                                                requestProviders: Seq[RequestProvider]
                                              ): UnsecuredRequestHandlerBuilder[I, A]
}

/**
  * Default implementation of the [[UnsecuredRequestHandler]].
  *
  * @param errorHandler The instance of the unsecured error handler.
  */
class DefaultUnsecuredRequestHandler(val errorHandler: UnsecuredErrorHandler)
  extends UnsecuredRequestHandler {

  /**
    * Applies the environment to the request handler stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @return A unsecured request handler builder.
    */
  override def apply[I <: Identity, A <: Authenticator](
                                                         identityService: IdentityService[I],
                                                         authenticatorService: AuthenticatorService[A],
                                                         requestProviders: Seq[RequestProvider]
                                                       ): UnsecuredRequestHandlerBuilder[I, A] =
    UnsecuredRequestHandlerBuilder[I, A](identityService, authenticatorService, requestProviders, errorHandler)
}

/**
  * Action builder implementation to provide the foundation for unsecured actions.
  *
  * @param requestHandler The request handler instance.
  * @tparam I The type of the identity.
  * @tparam A The type of the authenticator.
  */
case class UnsecuredActionBuilder[I <: Identity, A <: Authenticator ](requestHandler: UnsecuredRequestHandlerBuilder[I, A]) {

  /**
    * Creates a unsecured action builder with a new error handler in place.
    *
    * @param errorHandler An error handler instance.
    * @return A unsecured action builder.
    */
  def apply(errorHandler: UnsecuredErrorHandler): UnsecuredActionBuilder[I, A] = {
    UnsecuredActionBuilder[I, A](requestHandler(errorHandler))
  }

  /**
    * Invokes the block.
    *
    * @param request The current request.
    * @param block   The block of code to invoke.
    * @return A handler result.
    */
  def invokeBlock(request: Request, block: Request => Future[Response]): Future[Response] = {
    implicit val req = request
    val b = (r: Request) => block(r).map(r => HandlerResult(r))

    requestHandler(request)(b).map(_.result).rescue(requestHandler.errorHandler.exceptionHandler)
  }

}

/**
  * An action based on the [[UnsecuredRequestHandler]].
  */
trait UnsecuredAction {

  /**
    * The instance of the unsecured request handler.
    */
  val requestHandler: UnsecuredRequestHandler

  /**
    * Applies the environment to the action stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @param identityService      The identity service.
    * @param authenticatorService The authenticator service.
    * @return A secured action builder.
    */
  def apply[I <: Identity, A <: Authenticator ](identityService: IdentityService[I],
                                                authenticatorService: AuthenticatorService[A],
                                                requestProviders: Seq[RequestProvider]
                                               ): UnsecuredActionBuilder[I, A]

}

/**
  * Default implementation of the [[UnsecuredAction]].
  *
  * @param requestHandler The instance of the unsecured request handler.
  */
class DefaultUnsecuredAction(val requestHandler: UnsecuredRequestHandler) extends UnsecuredAction {

  /**
    * Applies the environment to the action stack.
    *
    * @tparam I The type of the identity.
    * @tparam A The type of the authenticator.
    * @param identityService      The identity service.
    * @param authenticatorService The authenticator service.
    * @return A secured action builder.
    */
  override def apply[I <: Identity, A <: Authenticator](
                                                          identityService: IdentityService[I],
                                                          authenticatorService: AuthenticatorService[A],
                                                          requestProviders: Seq[RequestProvider]
                                                        ): UnsecuredActionBuilder[I, A] = {

    UnsecuredActionBuilder[I, A](requestHandler[I, A](identityService, authenticatorService, requestProviders))

  }

}

/**
  * Error handler for unsecured actions.
  */
trait UnsecuredErrorHandler extends NotAuthorizedErrorHandler


/**
  * Default implementation of the [[UnsecuredErrorHandler]].
  *
  */
class DefaultUnsecuredErrorHandler
  extends UnsecuredErrorHandler
    with DefaultNotAuthorizedErrorHandler

