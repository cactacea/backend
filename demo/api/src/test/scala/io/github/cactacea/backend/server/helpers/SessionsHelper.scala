package io.github.cactacea.backend.server.helpers

import java.util.UUID

import com.twitter.finagle.http.Request
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.{APIServerSpec, Authentication}

trait SessionsHelper extends FeatureTest with CommonHelper {
  self: APIServerSpec =>

  def signUp(userName: String, password: String): Authentication = {
    val request = PostSignUp(userName, password, Request())
    val body = mapper.writePrettyString(request)
    val result = server.httpPost(
      path = "/sessions",
      headers = headers(),
      postBody = body
    )
    val user = mapper.parse[User](result.contentString)
    Authentication(user, result.headerMap.getOrNull(Config.auth.headerNames.authorizationKey))
  }

  def signIn(userName: String, password: String): User = {
    val udid = UUID.randomUUID().toString
    val path = s"/sessions?userName=${userName}&password=${password}&udid=${udid}"
    server.httpGetJson[User](
      path = path,
      headers = headers()
    )
  }

}
