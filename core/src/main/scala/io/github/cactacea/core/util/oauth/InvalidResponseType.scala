package io.github.cactacea.core.util.oauth

import com.twitter.finagle.oauth2.OAuthError

class InvalidResponseType(description: String = "") extends OAuthError(401, description) {
  override val errorType = "invalid_response_type"
}
