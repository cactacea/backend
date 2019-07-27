package io.github.cactacea.backend.server.helpers

import java.util.UUID

import com.twitter.finagle.http.Request
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.server.{APIServerSpec, Authentication}

trait SessionsHelper extends FeatureTest with CommonHelper {
  self: APIServerSpec =>

  def signUp(accountName: String, password: String): Authentication = {
    val request = PostSignUp(accountName, password, Request())
    val body = mapper.writePrettyString(request)
    val result = server.httpPost(
      path = "/sessions",
      headers = headers(),
      postBody = body
    )
    val account = mapper.parse[Account](result.contentString)
    Authentication(account, result.headerMap.getOrNull(Config.auth.headerNames.authorizationKey))
  }

  def signIn(accountName: String, password: String): Account = {
    val udid = UUID.randomUUID().toString
    val path = s"/sessions?accountName=${accountName}&password=${password}&udid=${udid}"
    server.httpGetJson[Account](
      path = path,
      headers = headers()
    )
  }

}
