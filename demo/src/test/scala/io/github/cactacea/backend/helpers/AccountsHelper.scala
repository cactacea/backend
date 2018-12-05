package io.github.cactacea.backend.helpers

import io.github.cactacea.backend.DemoServerSpec
import io.github.cactacea.backend.core.domain.models.Account

trait AccountsHelper extends CommonHelper {
  self: DemoServerSpec =>

  def getAccounts(accountName: Option[String], since: Option[Long], offset: Option[Int], count: Option[Int], accessToken: String): Array[Account] = {
    val path = s"/accounts${accountName.map("?accountName=" + _).getOrElse("")}"
    server.httpGetJson[Array[Account]](
      path = path,
      headers = headers(accessToken)
    )
  }

}
