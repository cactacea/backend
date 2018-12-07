package io.github.cactacea.backend.helpers

import com.twitter.finagle.http.Response
import io.github.cactacea.backend.DemoServerSpec
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.models.requests.session.PutSessionProfileImage

trait SessionHelper extends CommonHelper {
  self: DemoServerSpec =>

  def signOut(accessToken: String): Response = {
    server.httpDelete(
      path = "/session",
      headers = headers(accessToken)
    )
  }

  def updateProfileImage(id: MediumId, accessToken: String): Response = {
    val request = PutSessionProfileImage(id)
    val body = mapper.writePrettyString(request)
    server.httpPost(
      path = s"/session/profile_image",
      headers = headers(accessToken),
      postBody = body
    )
  }

}
