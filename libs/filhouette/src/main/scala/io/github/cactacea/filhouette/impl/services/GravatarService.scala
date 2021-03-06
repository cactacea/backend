/**
 * Original work: SecureSocial (https://github.com/jaliss/securesocial)
 * Copyright 2013 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Derivative work: Silhouette (https://github.com/mohiva/play-silhouette)
 * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
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
package io.github.cactacea.filhouette.impl.services

import java.net.URLEncoder._
import java.security.MessageDigest

import com.google.inject.Inject
import com.twitter.finagle.http.{Method, Request, Version}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.Logger
import io.github.cactacea.filhouette.api.services.AvatarService
import io.github.cactacea.filhouette.impl.services.GravatarService._

/**
 * Retrieves avatar URLs from the Gravatar service.
 *
 * @param httpLayer The HTTP layer implementation.
 * @param settings The Gravatar service settings.
 */
class GravatarService @Inject() (httpLayer: HttpClient, settings: GravatarServiceSettings = GravatarServiceSettings())
  extends AvatarService with Logger {

  /**
   * Retrieves the URL for the given email address.
   *
   * @param email The email address for the avatar.
   * @return Maybe an avatar URL or None if no avatar could be found for the given email address.
   */
  override def retrieveURL(email: String): Future[Option[String]] = {
    hash(email) match {
      case Some(hash) =>
        val encodedParams = settings.params.map { p => encode(p._1, "UTF-8") + "=" + encode(p._2, "UTF-8") }
        val url = (if (settings.secure) SecureURL else InsecureURL).format(hash, encodedParams.mkString("?", "&", ""))
        val request = Request(Version.Http11, Method.Get, url)
        httpLayer.execute(request).map { r =>
          r.status.code match {
            case 200 => Some(url)
            case code =>
              logger.info("[Silhouette] Gravatar API returns status code: " + code)
              None
          }
        }.rescue {
          case e: Exception =>
            logger.info("[Silhouette] Error invoking gravatar", e)
            Future.None
        }
      case None => Future.None
    }
  }

  /**
   * Builds the hash for the given email address.
   *
   * @param email The email address to build the hash for.
   * @return Maybe a hash for the given email address or None if the email address is empty.
   */
  private def hash(email: String): Option[String] = {
    val s = email.trim.toLowerCase
    if (s.length > 0) {
      Option(MessageDigest.getInstance(MD5).digest(s.getBytes).map("%02x".format(_)).mkString)
    } else {
      None
    }
  }
}

/**
 * The companion object.
 */
object GravatarService {
  val InsecureURL = "http://www.gravatar.com/avatar/%s%s"
  val SecureURL = "https://secure.gravatar.com/avatar/%s%s"
  val MD5 = "MD5"
}

/**
 * The gravatar service settings object.
 *
 * @param secure Indicates if the secure or insecure URL should be used to query the avatar images. Defaults to secure.
 * @param params A list of params to append to the URL.
 * @see https://en.gravatar.com/site/implement/images/
 */
case class GravatarServiceSettings(
  secure: Boolean = true,
  params: Map[String, String] = Map("d" -> "404"))
