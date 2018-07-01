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
package io.github.cactacea.filhouette.impl.providers.oauth2

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Inject
import com.twitter.finagle.http.{Method, Request, Version}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.util.Future
import io.github.cactacea.filhouette.api.LoginInfo
import io.github.cactacea.filhouette.impl.exceptions.ProfileRetrievalException
import io.github.cactacea.filhouette.impl.providers._
import io.github.cactacea.filhouette.impl.providers.oauth2.FacebookProvider.{ID, SpecifiedProfileError}
import io.github.cactacea.filhouette.impl.providers.oauth2.GoogleProvider._
import io.github.cactacea.filhouette.impl.util.Json
import io.github.cactacea.filhouette.impl.providers._

/**
 * Base Google OAuth2 Provider.
 *
 * @see https://developers.google.com/+/api/auth-migration#timetable
 * @see https://developers.google.com/+/api/auth-migration#oauth2login
 * @see https://developers.google.com/accounts/docs/OAuth2Login
 * @see https://developers.google.com/+/api/latest/people
 */
trait BaseGoogleProvider extends OAuth2Provider {

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
        val errorCode = error.get("code").asInt()
        throw new ProfileRetrievalException(SpecifiedProfileError.format(id, errorCode, errorMsg))
      } else {
        profileParser.parse(json, authInfo)
      }
    })
  }
}

/**
 * The profile parser for the common social profile.
 */
class GoogleProfileParser extends SocialProfileParser[JsonNode, CommonSocialProfile, OAuth2Info] {

  /**
    * Parses the social profile.
    *
    * @param json     The content returned from the provider.
    * @param authInfo The auth info to query the provider again for additional data.
    * @return The social profile from given result.
    */
  override def parse(json: JsonNode, authInfo: OAuth2Info) = Future.value {
    val userID =  json.get("id").asText()
    val firstName = Some(json.get("name").get("givenName").asText())
    val lastName = Some(json.get("name").get("familyName").asText())
    val fullName = Some(json.get("displayName").asText())
    val avatarURL = Some(json.get("image").get("url").asText())
    val isDefaultAvatar = json.get("image").get("isDefault").asBoolean(false)

//    // https://developers.google.com/+/api/latest/people#emails.type
//    val emailIndex = (json \ "emails" \\ "type").indexWhere(_.as[String] == "account")
//    val emailValue = if ((json \ "emails" \\ "value").isDefinedAt(emailIndex)) {
//      (json \ "emails" \\ "value")(emailIndex).asOpt[String]
//    } else {
//      None
//    }

    // TODO
    val emailIndex = ""
    val emailValue = Some("")

    CommonSocialProfile(
      loginInfo = LoginInfo(ID, userID),
      firstName = firstName,
      lastName = lastName,
      fullName = fullName,
      avatarURL = if (isDefaultAvatar) None else avatarURL, // skip the default avatar picture
      email = emailValue)
  }

}

/**
 * The Google OAuth2 Provider.
 *
 * @param stateHandler  The state provider implementation.
 * @param settings      The provider settings.
 */
class GoogleProvider(
                      protected val stateHandler: SocialStateHandler,
                      val settings: OAuth2Settings)
  extends BaseGoogleProvider with CommonSocialProfileBuilder {

  /**
    * The HTTP layer implementation.
    */
  @Inject var httpLayer: HttpClient = _

  /**
   * The type of this class.
   */
  type Self = GoogleProvider

  /**
   * The profile parser implementation.
   */
  val profileParser = new GoogleProfileParser

  /**
   * Gets a provider initialized with a new settings object.
   *
   * @param f A function which gets the settings passed and returns different settings.
   * @return An instance of the provider initialized with new settings.
   */
  def withSettings(f: (Settings) => Settings) = new GoogleProvider(stateHandler, f(settings))
}

/**
 * The companion object.
 */
object GoogleProvider {

  /**
   * The error messages.
   */
  val SpecifiedProfileError = "[Filhouette][%s] Error retrieving profile information. Error code: %s, message: %s"

  /**
   * The Google constants.
   */
  val ID = "google"
  val API = "https://www.googleapis.com/plus/v1/people/me?access_token=%s"
}
