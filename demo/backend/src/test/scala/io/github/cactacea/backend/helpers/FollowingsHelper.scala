package io.github.cactacea.backend.helpers

import com.twitter.finagle.http.Response
import io.github.cactacea.backend.BackendServerSpec
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

trait FollowingsHelper extends CommonHelper {
  self: BackendServerSpec =>

  def sessionFollowing(accessToken: String): Array[Account] = {
    val path = s"/session/following"
    server.httpGetJson[Array[Account]](
      path = path,
      headers = headers(accessToken)
    )
  }

  def follow(accountId: AccountId, accessToken: String): Response = {
    server.httpPost(
      path = s"/accounts/${accountId.value}/following",
      headers = headers(accessToken),
      postBody = ""
    )
  }

}
