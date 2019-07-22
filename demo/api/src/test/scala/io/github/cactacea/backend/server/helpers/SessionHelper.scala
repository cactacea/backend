package io.github.cactacea.backend.server.helpers

import com.twitter.finagle.http.Response
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.server.APIServerSpec
import io.github.cactacea.backend.server.models.requests.session.PutSessionProfileImage

trait SessionHelper extends CommonHelper {
  self: APIServerSpec =>

  def signOut(accessToken: String): Response = {
    server.httpDelete(
      path = "/session",
      headers = headers(accessToken)
    )
  }

  def updateProfileImage(id: MediumId, accessToken: String): Response = {
    val request = PutSessionProfileImage(id)
    val body = mapper.writePrettyString(request)
    server.httpPut(
      path = s"/session/profile_image",
      headers = headers(accessToken),
      putBody = body
    )
  }

}
