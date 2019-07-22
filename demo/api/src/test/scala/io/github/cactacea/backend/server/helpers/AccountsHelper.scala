package io.github.cactacea.backend.server.helpers

import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.server.APIServerSpec

trait AccountsHelper extends CommonHelper {
  self: APIServerSpec =>

  def getAccounts(accountName: Option[String], accessToken: String): Array[Account] = {
    val path = s"/accounts${accountName.map("?accountName=" + _).getOrElse("")}"
    server.httpGetJson[Array[Account]](
      path = path,
      headers = headers(accessToken)
    )
  }

}
