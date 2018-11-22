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
import com.twitter.finagle.http.Request
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.Future
import io.github.cactacea.filhouette.impl.providers.SocialStateItem.ItemStructure
import io.github.cactacea.filhouette.impl.providers.{SocialStateItem, SocialStateItemHandler}
import io.github.cactacea.filhouette.impl.providers.state.UserStateItemHandler._

import scala.reflect.ClassTag

/**
 * A default user state item where state is of type Map[String, String].
 */
case class UserStateItem(state: Map[String, String]) extends SocialStateItem

/**
 * The companion object of the [[UserStateItem]].
 */
object UserStateItem

/**
 * Handles user defined state.
 *
 * @param i     The user state item.
 * @tparam S The type of the user state.
 */
class UserStateItemHandler[S <: SocialStateItem](i: S)(implicit classTag: ClassTag[S]) extends SocialStateItemHandler {

  @Inject var objectMapper: FinatraObjectMapper = _

  /**
   * The item the handler can handle.
   */
  override type Item = S

  /**
   * Gets the state item the handler can handle.
   *
   * @return The state params the handler can handle.
   */
  override def item(): Future[Item] = Future.value(i)

  /**
   * Indicates if a handler can handle the given `SocialStateItem`.
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
  override def canHandle(item: ItemStructure)(implicit request: Request): Boolean = item.id == ID

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
    request: Request): Future[Item] = {
      Future.value(objectMapper.objectMapper.treeToValue(item.data, classTag.runtimeClass.asInstanceOf[Class[S]]))
  }
}

/**
 * The companion object.
 */
object UserStateItemHandler {

  /**
   * The ID of the state handler.
   */
  val ID = "user-state"
}
