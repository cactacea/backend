package io.github.cactacea.backend.helpers

import io.github.cactacea.backend.BackendServerSpec
import io.github.cactacea.backend.core.domain.models.Account

trait AccountsHelper extends CommonHelper {
  self: BackendServerSpec =>

  def getAccounts(accountName: Option[String], accessToken: String): Array[Account] = {
    val path = s"/accounts${accountName.map("?accountName=" + _).getOrElse("")}"
    server.httpGetJson[Array[Account]](
      path = path,
      headers = headers(accessToken)
    )
  }

}
