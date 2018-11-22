/**
 * Copyright 2017 Mohiva Organisation (license at mohiva dot com)
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
package io.github.cactacea.filhouette.impl.providers.state

import com.google.inject.Inject
import com.twitter.conversions.time._
import com.twitter.finagle.http.{Cookie, Request, Response}
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util._
import io.github.cactacea.filhouette.api.Logger
import io.github.cactacea.filhouette.api.crypto.Signer
import io.github.cactacea.filhouette.api.util.IDGenerator
import io.github.cactacea.filhouette.impl.exceptions.OAuth2StateException
import io.github.cactacea.filhouette.impl.providers.SocialStateItem.ItemStructure
import io.github.cactacea.filhouette.impl.providers.state.CsrfStateItemHandler._
import io.github.cactacea.filhouette.impl.providers.{PublishableSocialStateItemHandler, SocialStateItem, SocialStateItemHandler}

/**
 * The item the handler can handle.
 *
 * @param token A unique token used to protect the application against CSRF attacks.
 */
case class CsrfStateItem(token: String) extends SocialStateItem

/**
 * Protects the application against CSRF attacks.
 *
 * The handler stores a unique token in provider state and the same token in a signed client side cookie. After the
 * provider redirects back to the application both tokens will be compared. If both tokens are the same than the
 * application can trust the redirect source.
 *
 * @param settings     The state settings.
 * @param idGenerator  The ID generator used to create the state value.
 * @param signer       The signer implementation.
 */
class CsrfStateItemHandler(
  settings: CsrfStateSettings,
  idGenerator: IDGenerator,
  signer: Signer
) extends SocialStateItemHandler with Logger
  with PublishableSocialStateItemHandler {

  @Inject var objectMapper: FinatraObjectMapper = _

  /**
   * The item the handler can handle.
   */
  override type Item = CsrfStateItem

  /**
   * Gets the state item the handler can handle.
   *
   * @return The state params the handler can handle.
   */
  override def item(): Future[Item] = idGenerator.generate.map(CsrfStateItem.apply)

  /**
   * Indicates if a handler can handle the given [[SocialStateItem]].
   *
   * This method should check if the [[serialize]] method of this handler can serialize the given
   * unserialized state item.
   *
   * @param item The item to check for.
   * @return `Some[Item]` casted state item if the handler can handle the given state item, `None` otherwise.
   */
  override def canHandle(item: SocialStateItem): Option[Item] = item match {
    case i: Item => Some(i)
    case _       => None
  }

  /**
   * Indicates if a handler can handle the given unserialized state item.
   *
   * This method should check if the [[unserialize]] method of this handler can unserialize the given
   * serialized state item.
   *
   * @param item    The item to check for.
   * @param request The request instance to get additional data to validate against.
   * @return True if the handler can handle the given state item, false otherwise.
   */
  override def canHandle(item: ItemStructure)(implicit request: Request): Boolean = {
    item.id == ID && {
      clientState match {
        case Return(i) => i == objectMapper.objectMapper.treeToValue(item.data, classOf[Item])
        case Throw(e) =>
          logger.warn(e.getMessage, e)
          false
      }
    }
  }

  /**
   * Returns a serialized value of the state item.
   *
   * @param item The state item to serialize.
   * @return The serialized state item.
   */
  override def serialize(item: Item): ItemStructure = ItemStructure(ID, objectMapper.objectMapper.valueToTree(item))

  /**
   * Unserializes the state item.
   *
   * @param item    The state item to unserialize.
   * @param request The request instance to get additional data to validate against.
   * @return The unserialized state item.
   */
  override def unserialize(item: ItemStructure)(
    implicit
    request: Request
  ): Future[Item] = {
    Future.value(objectMapper.objectMapper.treeToValue(item.data, classOf[Item]))
  }

  /**
   * Publishes the CSRF token to the client.
   *
   * @param item    The item to publish.
   * @param result  The result to send to the client.
   * @param request The current request.
   * @return The result to send to the client.
   */
  override def publish(item: Item, result: Response)(implicit request: Request): Response = {
    result.addCookie(new Cookie(
      settings.cookieName,
      signer.sign(item.token),
      settings.cookieDomain,
      settings.cookiePath,
      Some(settings.expirationTime),
      settings.secureCookie,
      settings.httpOnlyCookie
    ))
    result
  }

  /**
   * Gets the CSRF token from the cookie.
   *
   * @param request The request header.
   * @return The CSRF token on success, otherwise a failure.
   */
  private def clientState(implicit request: Request): Try[Item] = {
    request.cookies.get(settings.cookieName) match {
      case Some(cookie) => signer.extract(cookie.value).map(token => CsrfStateItem(token))
      case None         => Throw(new OAuth2StateException(ClientStateDoesNotExists.format(settings.cookieName)))
    }
  }
}

/**
 * The companion object.
 */
object CsrfStateItemHandler {

  /**
   * The ID of the handler.
   */
  val ID = "csrf-state"

  /**
   * The error messages.
   */
  val ClientStateDoesNotExists = "[Filhouette][CsrfStateItemHandler] State cookie doesn't exists for name: %s"
}

/**
 * The settings for the Csrf State.
 *
 * @param cookieName     The cookie name.
 * @param cookiePath     The cookie path.
 * @param cookieDomain   The cookie domain.
 * @param secureCookie   Whether this cookie is secured, sent only for HTTPS requests.
 * @param httpOnlyCookie Whether this cookie is HTTP only, i.e. not accessible from client-side JavaScript code.
 * @param expirationTime State expiration. Defaults to 5 minutes which provides sufficient time to log in, but
 *                       not too much. This is a balance between convenience and security.
 */
case class CsrfStateSettings(
  cookieName: String = "CsrfState",
  cookiePath: Option[String] = Some("/"),
  cookieDomain: Option[String] = None,
  secureCookie: Boolean = true,
  httpOnlyCookie: Boolean = true,
  expirationTime: Duration = 5 minutes
)
