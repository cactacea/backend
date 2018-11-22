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
package io.github.cactacea.filhouette.api

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.exceptions.{NotAuthenticatedException, NotAuthorizedException}

/**
  * Silhouette error handler.
  */
sealed trait ErrorHandler {

  /**
    * Calls the error handler methods based on a caught exception.
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  def exceptionHandler(implicit request: Request): PartialFunction[Throwable, Future[Response]]
}

/**
  * Handles errors when a user is authenticated but not authorized.
  */
trait NotAuthorizedErrorHandler extends ErrorHandler {

  /**
    * Exception handler which translates an [[io.github.cactacea.filhouette.api.exceptions.NotAuthorizedException]]
    * into a 403 Forbidden result.
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  def exceptionHandler(implicit request: Request): PartialFunction[Throwable, Future[Response]] = {
    case e: NotAuthorizedException => onNotAuthorized
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  def onNotAuthorized(implicit request: Request): Future[Response]
}

/**
  * Handles not authorized requests in a default way.
  */
trait DefaultNotAuthorizedErrorHandler
  extends NotAuthorizedErrorHandler
    with DefaultErrorHandler
    //    with I18nSupport
    with Logger {

  /**
    * @inheritdoc
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  override def exceptionHandler(implicit request: Request) = {
    case e: NotAuthorizedException =>
      logger.info(e.getMessage, e)
      super.exceptionHandler(request)(e)
  }

  /**
    * @inheritdoc
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: Request) = {
    logger.debug("[Filhouette] Unauthorized user trying to access '%s'".format(request.uri))
    Future.value(Response(request.version, Status.Unauthorized))
    //    produceResponse(Forbidden, Messages("silhouette.not.authorized"))
  }
}

/**
  * Handles errors when a user is not authenticated.
  */
trait NotAuthenticatedErrorHandler extends ErrorHandler {

  /**
    * Exception handler which translates an [com.mohiva.play.silhouette.api.exceptions.NotAuthenticatedException]]
    * into a 401 Unauthorized result.
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  def exceptionHandler(implicit request: Request): PartialFunction[Throwable, Future[Response]] = {
    case e: NotAuthenticatedException => onNotAuthenticated
  }

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  def onNotAuthenticated(implicit request: Request): Future[Response]
}

/**
  * Handles not authenticated requests in a default way.
  */
trait DefaultNotAuthenticatedErrorHandler
  extends NotAuthenticatedErrorHandler
    with DefaultErrorHandler
    //    with I18nSupport
    with Logger {

  /**
    * @inheritdoc
    *
    * @param request The request header.
    * @return A partial function which maps an exception to a Play result.
    */
  override def exceptionHandler(implicit request: Request) = {
    case e: NotAuthenticatedException =>
      logger.info(e.getMessage, e)
      super.exceptionHandler(request)(e)
  }

  /**
    * @inheritdoc
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(implicit request: Request) = {
    logger.debug("[Filhouette] Unauthenticated user trying to access '%s'".format(request.uri))
    Future.value(Response(request.version, Status.Unauthorized))
    //    produceResponse(Unauthorized, Messages("silhouette.not.authenticated"))
  }
}

/**
  * Provides the base implementation for the default error handlers.
  */
trait DefaultErrorHandler
