package io.github.cactacea.backend.helpers

import com.twitter.finagle.http.Response
import io.github.cactacea.backend.DemoServerSpec
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

trait FollowsHelper extends CommonHelper {
  self: DemoServerSpec =>

  def sessionFollows(accessToken: String): Array[Account] = {
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
