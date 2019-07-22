package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Response
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.server.APIServerSpec

trait FollowsHelper extends CommonHelper {
  self: APIServerSpec =>

  def sessionFollow(accessToken: String): Array[Account] = {
    val path = s"/session/follows"
    server.httpGetJson[Array[Account]](
      path = path,
      headers = headers(accessToken)
    )
  }

  def follow(accountId: AccountId, accessToken: String): Response = {
    server.httpPost(
      path = s"/accounts/${accountId.value}/follows",
      headers = headers(accessToken),
      postBody = ""
    )
  }

}
