/**
  * Original work: SecureSocial (https://github.com/jaliss/securesocial)
  * Copyright 2013 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
  *
  * Derivative work: Silhouette (https://github.com/mohiva/play-silhouette)
  * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
  *
  * Derivative work: Filhouette (https://github.com/cactacea)
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

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.services.{AuthenticatorService, IdentityService}

import scala.language.higherKinds

/**
  * A result which can transport a result as also additional data through the request handler process.
  *
  * @param result A Play Framework result.
  * @param data Additional data to transport in the result.
  * @tparam T The type of the data.
  */
case class HandlerResult[+T](result: Response, data: Option[T] = None)

/**
  * Base implementation for building request handlers.
  *
  * The base implementations to handle secured endpoints are encapsulated into request handlers which
  * can execute an arbitrary block of code and must return a HandlerResult. This HandlerResult consists
  * of a normal Play result and arbitrary additional data which can be transported out of these handlers.
  *
  * @tparam A The type of the authenticator.
  * @tparam I The type of the identity.
  */
trait RequestHandlerBuilder[I <: Identity, A <: Authenticator, +R] {

  /**
    * Provides an `extract` method on an `Either` which contains the same types.
    */
  protected implicit class ExtractEither[T](r: Either[T, T]) {
    def extract: T = r.fold(identity, identity)
  }

  /**
    * Gets the identity service implementation.
    *
    * @return The identity service implementation.
    */
  def identityService: IdentityService[I]

  /**
    * Gets the authenticator service implementation.
    *
    * @return The authenticator service implementation.
    */
  def authenticatorService: AuthenticatorService[A]

  /**
    * Gets the list of request providers.
    *
    * @return The list of request providers.
    */
  def requestProviders: Seq[RequestProvider]

  /**
    * Constructs a request handler with default content.
    *
    * @param block The block of code to invoke.
    * @param request The current request.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  final def apply[T](block: R => Future[HandlerResult[T]])(implicit request: Request): Future[HandlerResult[T]] = {
    invokeBlock(block)
  }

  /**
    * Constructs a request handler with the content of the given request.
    *
    * @param request The current request.
    * @param block The block of code to invoke.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  final def apply[T](request: Request)(block: R => Future[HandlerResult[T]]): Future[HandlerResult[T]] = {
    invokeBlock(block)(request)
  }

  /**
    * Invoke the block.
    *
    * This is the main method that an request handler has to implement.
    *
    * @param block The block of code to invoke.
    * @param request The current request.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  def invokeBlock[T](block: R => Future[HandlerResult[T]])(implicit request: Request): Future[HandlerResult[T]]

  //  /**
//    * Constructs a request handler with default content.
//    *
//    * @param block The block of code to invoke.
//    * @param request The current request.
//    * @tparam T The type of the data included in the handler result.
//    * @return A handler result.
//    */
//  final def apply[T](block: Request => Future[HandlerResult[T]])(implicit request: Request): Future[HandlerResult[T]] = {
//    invokeBlock(block)
//  }
//
//  /**
//    * Constructs a request handler with the content of the given request.
//    *
//    * @param request The current request.
//    * @param block The block of code to invoke.
//    * @tparam T The type of the data included in the handler result.
//    * @return A handler result.
//    */
//  final def apply[T](request: Request)(block: Request => Future[HandlerResult[T]]): Future[HandlerResult[T]] = {
//    invokeBlock(block)(request)
//  }
//
//  /**
//    * Invoke the block.
//    *
//    * This is the main method that an request handler has to implement.
//    *
//    * @param block The block of code to invoke.
//    * @param request The current request.
//    * @tparam T The type of the data included in the handler result.
//    * @return A handler result.
//    */
//  def invokeBlock[T](block: Request => Future[HandlerResult[T]])(implicit request: Request): Future[HandlerResult[T]]

  /**
    * Handles a block for an authenticator.
    *
    * Invokes the block with the authenticator and handles the result. See `handleInitializedAuthenticator` and
    * `handleUninitializedAuthenticator` methods too see how the different authenticator types will be handled.
    *
    * @param authenticator An already initialized authenticator on the left and a new authenticator on the right.
    * @param block The block to handle with the authenticator.
    * @param request The current request header.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  protected def handleBlock[T](authenticator: Either[A, A], block: A => Future[HandlerResult[T]])(implicit request: Request) = {
    authenticator match {
      case Left(a)  => handleInitializedAuthenticator(a, block)
      case Right(a) => handleUninitializedAuthenticator(a, block)
    }
  }

  /**
    * Handles the authentication of an identity.
    *
    * As first it checks for authenticators in requests, then it tries to authenticate against a request provider.
    * This method marks the returned authenticators by returning already initialized authenticators on the
    * left and new authenticators on the right. All new authenticators must be initialized later in the flow,
    * with the result returned from the invoked block.
    *
    * @param request The current request.
    * @return A tuple which consists of (maybe the existing authenticator on the left or a
    *         new authenticator on the right -> maybe the identity).
    */
  protected def handleAuthentication(implicit request: Request): Future[(Option[Either[A, A]], Option[I])] = {
    authenticatorService.retrieve.flatMap {
      // A valid authenticator was found so we retrieve also the identity
      case Some(a) if a.isValid  => identityService.retrieve(a.loginInfo).map(i => Some(Left(a)) -> i)
      // An invalid authenticator was found so we needn't retrieve the identity
      case Some(a) if !a.isValid => Future.value(Some(Left(a)) -> None)
      // No authenticator was found so we try to authenticate with a request provider
      case None => handleRequestProviderAuthentication.flatMap {
        // Authentication was successful, so we retrieve the identity and create a new authenticator for it
        case Some(loginInfo) => identityService.retrieve(loginInfo).flatMap { i =>
          authenticatorService.create(loginInfo).map(a => Some(Right(a)) -> i)
        }
        // No identity and no authenticator was found
        case None => Future.value(None -> None)
      }
    }
  }

  /**
    * Handles already initialized authenticators.
    *
    * The authenticator handled by this method was found in the current request. So it was initialized on
    * a previous request and must now be updated if it was touched and no authenticator result was found.
    *
    * @param authenticator The authenticator to handle.
    * @param block The block to handle with the authenticator.
    * @param request The current request header.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  private def handleInitializedAuthenticator[T](authenticator: A, block: A => Future[HandlerResult[T]])(implicit request: Request) = {
    val auth = authenticatorService.touch(authenticator)
    block(auth.fold(identity, identity)).flatMap {
      case hr @ HandlerResult(pr: Response, _) => Future.value(hr)
      case hr @ HandlerResult(pr, _) => auth match {
        // Authenticator was touched so we update the authenticator and maybe the result
        case Left(a)  => authenticatorService.update(a, pr).map(pr => hr.copy(pr))
        // Authenticator was not touched so we return the original result
        case Right(a) => Future.value(hr)
      }
    }
  }

  /**
    * Handles not initialized authenticators.
    *
    * The authenticator handled by this method was newly created after authentication with a request provider.
    * So it must be initialized with the result of the invoked block if no authenticator result was found.
    *
    * @param authenticator The authenticator to handle.
    * @param block The block to handle with the authenticator.
    * @param request The current request header.
    * @tparam T The type of the data included in the handler result.
    * @return A handler result.
    */
  private def handleUninitializedAuthenticator[T](authenticator: A, block: A => Future[HandlerResult[T]])(implicit request: Request) = {
    block(authenticator).flatMap {
      case hr @ HandlerResult(pr: Response, _) => Future.value(hr)
      case hr @ HandlerResult(pr, _) =>
        authenticatorService.init(authenticator).flatMap { value =>
          authenticatorService.embed(value, pr)
        }.map(pr => hr.copy(pr))
    }
  }

  /**
    * Handles the authentication with the request providers.
    *
    * Silhouette supports chaining of request providers. So if more as one request provider is defined
    * it tries to authenticate until one provider returns an identity. The order of the providers
    * isn't guaranteed.
    *
    * @param request The current request.
    * @return Some identity or None if authentication was not successful.
    */
  private def handleRequestProviderAuthentication(implicit request: Request): Future[Option[LoginInfo]] = {
    def auth(providers: Seq[RequestProvider]): Future[Option[LoginInfo]] = {
      providers match {
        case Nil => Future.None
        case h :: t => h.authenticate(request).flatMap {
          case Some(i) => Future.value(Some(i))
          case None    => if (t.isEmpty) Future.None else auth(t)
        }
      }
    }

    auth(requestProviders)
  }
}
