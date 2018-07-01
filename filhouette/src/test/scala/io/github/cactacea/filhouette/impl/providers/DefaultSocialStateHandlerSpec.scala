/**
 * Copyright 2015 Mohiva Organisation (license at mohiva dot com)
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
package io.github.cactacea.filhouette.impl.providers

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future, Return, Throw}
import io.github.cactacea.filhouette.api.crypto.Signer
import io.github.cactacea.filhouette.api.exceptions.ProviderException
import io.github.cactacea.filhouette.impl.providers
import io.github.cactacea.filhouette.impl.providers.DefaultSocialStateHandler._
import io.github.cactacea.filhouette.impl.providers._
import io.github.cactacea.filhouette.impl.providers.SocialStateItem.ItemStructure
import org.specs2.matcher.JsonMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 *  Test case for the [[DefaultSocialStateHandler]] class.
 */
class DefaultSocialStateHandlerSpec extends Specification with Mockito with JsonMatchers {

  "The `withHandler` method" should {
    "return a new state handler with the given item handler added" in new Context {
      val newHandler = mock[SocialStateItemHandler].smart

      stateHandler.handlers.size must be equalTo 2
      stateHandler.withHandler(newHandler).handlers.size must be equalTo 3
    }
  }

  "The `state` method" should {
    "return the social state" in new Context {
      Default.itemHandler.item returns Future.value(Default.item)
      Publishable.itemHandler.item returns Future.value(Publishable.item)

      Await.result(stateHandler.state) must be equalTo state
    }
  }

  "The `serialize` method" should {
    "return an empty string if no handler is registered" in new Context {
      override val stateHandler = new DefaultSocialStateHandler(Set(), signer)

      stateHandler.serialize(state) must be equalTo ""
    }

    "return an empty string if the items are empty" in new Context {
      stateHandler.serialize(SocialState(Set())) must be equalTo ""
    }

    "return the serialized social state" in new Context {
      Default.itemHandler.canHandle(Publishable.item) returns None
      Default.itemHandler.canHandle(Default.item) returns Some(Default.item)
      Default.itemHandler.serialize(Default.item) returns Default.structure

      Publishable.itemHandler.canHandle(Default.item) returns None
      Publishable.itemHandler.canHandle(Publishable.item) returns Some(Publishable.item)
      Publishable.itemHandler.serialize(Publishable.item) returns Publishable.structure

      stateHandler.serialize(state) must be equalTo s"${Publishable.structure.asString}.${Default.structure.asString}"
    }
  }

  "The `unserialize` method" should {
    "omit state validation if no handler is registered" in new Context {
      override val stateHandler = new DefaultSocialStateHandler(Set(), signer)
      implicit val request = Request()

      Await.result(stateHandler.unserialize(""))

      there was no(signer).extract(any[String])
    }

    "throw an Exception for an empty string" in new Context {
      implicit val request = Request()

      Await.result(stateHandler.unserialize("")) must throwA[RuntimeException].like {
        case e =>
          e.getMessage must startWith("Wrong state format")
      }
    }

    "throw an ProviderException if the serialized item structure cannot be extracted" in new Context {
      implicit val request = Request()
      val serialized = s"some-wired-content"

      Await.result(stateHandler.unserialize(serialized)) must throwA[ProviderException].like {
        case e =>
          e.getMessage must startWith(ItemExtractionError.format(serialized))
      }
    }

    "throw an ProviderException if none of the item handlers can handle the given state" in new Context {
      implicit val request = Request()
      val serialized = s"${Default.structure.asString}"

      Default.itemHandler.canHandle(any[ItemStructure])(any) returns false
      Publishable.itemHandler.canHandle(any[ItemStructure])(any) returns false

      Await.result(stateHandler.unserialize(serialized)) must throwA[ProviderException].like {
        case e =>
          e.getMessage must startWith(MissingItemHandlerError.format(Default.structure))
      }
    }

    "return the unserialized social state" in new Context {
      implicit val request = Request()
      val serialized = s"${Default.structure.asString}.${Publishable.structure.asString}"

      Default.itemHandler.canHandle(Publishable.structure) returns false
      Default.itemHandler.canHandle(Default.structure) returns true
      Default.itemHandler.unserialize(Default.structure) returns Future.value(Default.item)

      Publishable.itemHandler.canHandle(Default.structure) returns false
      Publishable.itemHandler.canHandle(Publishable.structure) returns true
      Publishable.itemHandler.unserialize(Publishable.structure) returns Future.value(Publishable.item)

      Await.result(stateHandler.unserialize(serialized)) must be equalTo providers.SocialState(Set(Default.item, Publishable.item))
    }
  }

  "The `publish` method" should {
    "should publish the state with the publishable handler that is responsible for the item" in new Context {
      implicit val request = Request()
      val result = Response(Status.Ok)
      val publishedResult = Response(Status.Ok)
      publishedResult.headerMap.set("X-PUBLISHED", "true")
      Publishable.itemHandler.publish(Publishable.item, result) returns publishedResult
      Publishable.itemHandler.canHandle(Default.item) returns None
      Publishable.itemHandler.canHandle(Publishable.item) returns Some(Publishable.item)

      stateHandler.publish(result, state) must be equalTo publishedResult
    }

    "should not publish the state if no publishable handler is responsible" in new Context {
      implicit val request = Request()
      val result = Response(Status.Ok)

      Publishable.itemHandler.canHandle(Default.item) returns None
      Publishable.itemHandler.canHandle(Publishable.item) returns None

      stateHandler.publish(result, state) must be equalTo result
    }
  }

  /**
   * The context.
   */
  trait Context extends Scope {

    /**
     * A default handler implementation.
     */
    case class DefaultItem() extends SocialStateItem
    trait DefaultItemHandler extends SocialStateItemHandler {
      type Item = DefaultItem
    }
    object Default {
      val itemHandler = mock[DefaultItemHandler].smart
      val item = DefaultItem()
      val structure = ItemStructure("default", JsonNodeFactory.instance.objectNode())
    }

    /**
     * A publishable handler implementation.
     */
    case class PublishableItem() extends SocialStateItem
    trait PublishableItemHandler extends SocialStateItemHandler with PublishableSocialStateItemHandler {
      type Item = PublishableItem
    }
    object Publishable {
      val itemHandler = mock[PublishableItemHandler].smart
      val item = PublishableItem()
      val structure = ItemStructure("publishable", JsonNodeFactory.instance.objectNode())
    }

    /**
     * The signer implementation.
     *
     * The signer returns the same value as passed to the methods. This is enough for testing.
     */
    val signer = {
      val c = mock[Signer].smart
      c.sign(any) answers { p => p.asInstanceOf[String] }
      c.extract(any) answers { p =>
        p.asInstanceOf[String] match {
          case "" => Throw(new RuntimeException("Wrong state format"))
          case s  => Return(s)
        }
      }
      c
    }

    /**
     * The state.
     */
    val state = providers.SocialState(Set(Publishable.item, Default.item))

    /**
     * The state handler to test.
     */
    val stateHandler = new DefaultSocialStateHandler(Set(Publishable.itemHandler, Default.itemHandler), signer)
  }
}
