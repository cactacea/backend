package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Response
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.server.APIServerSpec

trait FollowsHelper extends CommonHelper {
  self: APIServerSpec =>

  def sessionFollow(accessToken: String): Array[User] = {
    val path = s"/session/follows"
    server.httpGetJson[Array[User]](
      path = path,
      headers = headers(accessToken)
    )
  }

  def follow(userId: UserId, accessToken: String): Response = {
    server.httpPost(
      path = s"/users/${userId.value}/follows",
      headers = headers(accessToken),
      postBody = ""
    )
  }

}
