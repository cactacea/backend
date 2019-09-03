package io.github.cactacea.backend.server.helpers

import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.server.APIServerSpec

trait UsersHelper extends CommonHelper {
  self: APIServerSpec =>

  def getUsers(userName: Option[String], accessToken: String): Array[User] = {
    val path = s"/users${userName.map("?userName=" + _).getOrElse("")}"
    server.httpGetJson[Array[User]](
      path = path,
      headers = headers(accessToken)
    )
  }

}
