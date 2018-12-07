package io.github.cactacea.backend.helpers

import java.util.UUID

import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.DemoServerSpec
import io.github.cactacea.backend.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.models.responses.Authentication

trait SessionsHelper extends FeatureTest with CommonHelper {
  self: DemoServerSpec =>

  def signUp(accountName: String, password: String): Authentication = {
    val uuid = UUID.randomUUID().toString
    val request = PostSignUp(accountName, password, uuid, None)
    val body = mapper.writePrettyString(request)
    server.httpPostJson[Authentication](
      path = "/sessions",
      headers = headers(),
      postBody = body
    )
  }

  def signIn(accountName: String, password: String): Authentication = {
    val udid = UUID.randomUUID().toString
    val path = s"/sessions?accountName=${accountName}&password=${password}&udid=${udid}"
    server.httpGetJson[Authentication](
      path = path,
      headers = headers()
    )
  }

}
