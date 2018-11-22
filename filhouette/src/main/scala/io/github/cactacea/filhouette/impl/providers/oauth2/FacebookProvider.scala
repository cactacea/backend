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
package io.github.cactacea.filhouette.impl.providers.oauth2

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Inject
import com.twitter.finagle.http.{Method, Request, Version}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.impl.exceptions.ProfileRetrievalException
import io.github.cactacea.filhouette.impl.providers._
import io.github.cactacea.filhouette.impl.providers.oauth2.FacebookProvider._
import io.github.cactacea.filhouette.impl.util.Json

/**
 * Base Facebook OAuth2 Provider.
 *
 * @see https://developers.facebook.com/tools/explorer
 * @see https://developers.facebook.com/docs/graph-api/reference/user
 * @see https://developers.facebook.com/docs/facebook-login/access-tokens
 */
trait BaseFacebookProvider extends OAuth2Provider {

  /**
   * The content type to parse a profile from.
   */
  override type Content = JsonNode

  /**
   * The provider ID.
   */
  override val id = ID

  /**
   * Defines the URLs that are needed to retrieve the profile data.
   */
  override protected val urls = Map("api" -> settings.apiURL.getOrElse(API))

  /**
   * Builds the social profile.
   *
   * @param authInfo The auth info received from the provider.
   * @return On success the build social profile, otherwise a failure.
   */
  override protected def buildProfile(authInfo: OAuth2Info): Future[Profile] = {
    val request = Request(Version.Http11, Method.Get, urls("api").format(authInfo.accessToken))
    httpLayer.execute(request).flatMap({ response =>
      val json = Json.obj(response.contentString)
      val error = json.get("error")
      if (error != null) {
        val errorMsg = error.get("message").asText()
        val errorType = error.get("type").asText()
        val errorCode = error.get("code").asInt()
        Future.exception(new ProfileRetrievalException(SpecifiedProfileError.format(id, errorMsg, errorType, errorCode)))
      } else {
        profileParser.parse(json, authInfo)
      }
    })
  }
}

/**
 * The profile parser for the common social profile.
 */
class FacebookProfileParser extends SocialProfileParser[JsonNode, CommonSocialProfile, OAuth2Info] {

  /**
   * Parses the social profile.
   *
   * @param json     The content returned from the provider.
   * @param authInfo The auth info to query the provider again for additional data.
   * @return The social profile from given result.
   */
  override def parse(json: JsonNode, authInfo: OAuth2Info) = Future.value {
    val userID =  json.get("id").asText()
    val firstName = Some(json.get("first_name").asText())
    val lastName = Some(json.get("last_name").asText())
    val fullName = Some(json.get("name").asText())
    val avatarURL = Some(json.get("picture").get("data").get("url").asText())
    val email = Some(json.get("email").asText())

    CommonSocialProfile(
      loginInfo = LoginInfo(ID, userID),
      firstName = firstName,
      lastName = lastName,
      fullName = fullName,
      avatarURL = avatarURL,
      email = email)
  }
}

/**
 * The Facebook OAuth2 Provider.
 *
 * @param stateHandler  The state provider implementation.
 * @param settings      The provider settings.
 */
class FacebookProvider (
                        protected val stateHandler: SocialStateHandler,
                        val settings: OAuth2Settings)
  extends BaseFacebookProvider with CommonSocialProfileBuilder {

  /**
    * The HTTP layer implementation.
    */
  @Inject var httpLayer: HttpClient = _

  /**
   * The type of this class.
   */
  override type Self = FacebookProvider

  /**
   * The profile parser implementation.
   */
  override val profileParser = new FacebookProfileParser

  /**
   * Gets a provider initialized with a new settings object.
   *
   * @param f A function which gets the settings passed and returns different settings.
   * @return An instance of the provider initialized with new settings.
   */
  override def withSettings(f: (Settings) => Settings) = new FacebookProvider(stateHandler, f(settings))
}

/**
 * The companion object.
 */
object FacebookProvider {

  /**
   * The error messages.
   */
  val SpecifiedProfileError = "[Filhouette][%s] Error retrieving profile information. Error message: %s, type: %s, code: %s"

  /**
   * The Facebook constants.
   */
  val ID = "facebook"
  val API = "https://graph.facebook.com/v2.3/me?fields=name,first_name,last_name,picture,email&return_ssl_resources=1&access_token=%s"
}
