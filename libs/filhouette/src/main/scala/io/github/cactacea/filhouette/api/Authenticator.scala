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

import com.twitter.util.Duration
import io.github.cactacea.filhouette.api.Authenticator.Implicits._
import org.joda.time.DateTime

/**
  * An authenticator recognizes an authenticated user.
  */
trait Authenticator {

  /**
    * The Type of the generated value an authenticator will be serialized to.
    */
  type Value

  /**
    * The type of the settings an authenticator can handle.
    */
  type Settings

  /**
    * Gets the linked login info for an identity.
    *
    * @return The linked login info for an identity.
    */
  def loginInfo: LoginInfo

  /**
    * Checks if the authenticator valid.
    *
    * @return True if the authenticator valid, false otherwise.
    */
  def isValid: Boolean
}

/**
  * The `Authenticator` companion object.
  */
object Authenticator {

  /**
    * Some implicits.
    */
  object Implicits {

    /**
      * Defines additional methods on an `DateTime` instance.
      *
      * @param dateTime The `DateTime` instance on which the additional methods should be defined.
      */
    implicit class RichDateTime(dateTime: DateTime) {

      /**
        * Adds a duration to a date/time.
        *
        * @param duration The duration to add.
        * @return A date/time instance with the added duration.
        */
      def +(duration: Duration): DateTime = {
        dateTime.plusSeconds(duration.inSeconds)
      }

      /**
        * Subtracts a duration from a date/time.
        *
        * @param duration The duration to subtract.
        * @return A date/time instance with the subtracted duration.
        */
      def -(duration: Duration): DateTime = {
        dateTime.minusSeconds(duration.inSeconds)
      }
    }
  }
}

/**
  * An authenticator which can be stored in a backing store.
  */
trait StorableAuthenticator extends Authenticator {

  /**
    * Gets the ID to reference the authenticator in the backing store.
    *
    * @return The ID to reference the authenticator in the backing store.
    */
  def id: String
}

/**
  * An authenticator that may expire.
  */
trait ExpirableAuthenticator extends Authenticator {

  /**
    * The last used date/time.
    */
  val lastUsedDateTime: DateTime

  /**
    * The expiration date/time.
    */
  val expirationDateTime: DateTime

  /**
    * The duration an authenticator can be idle before it timed out.
    */
  val idleTimeout: Option[Duration]

  /**
    * Checks if the authenticator isn't expired and isn't timed out.
    *
    * @return True if the authenticator isn't expired and isn't timed out.
    */
  override def isValid: Boolean = !isExpired && !isTimedOut

  /**
    * Checks if the authenticator is expired. This is an absolute timeout since the creation of
    * the authenticator.
    *
    * @return True if the authenticator is expired, false otherwise.
    */
  def isExpired: Boolean = expirationDateTime.isBeforeNow

  /**
    * Checks if the time elapsed since the last time the authenticator was used, is longer than
    * the maximum idle timeout specified in the properties.
    *
    * @return True if sliding window expiration is activated and the authenticator is timed out, false otherwise.
    */
  def isTimedOut: Boolean = idleTimeout.isDefined && (lastUsedDateTime + idleTimeout.get).isBeforeNow
}
