package io.github.cactacea.backend.server.helpers

import java.nio.file.{Files, Paths}

import com.twitter.finagle.http.FileElement
import com.twitter.io.Buf
import io.github.cactacea.backend.server.APIServerSpec
import io.github.cactacea.backend.server.models.responses.MediumCreated

trait MediumsHelper extends CommonHelper {
  self: APIServerSpec =>

  def uploadMedium(path: String, resourceName: String, accessToken: String): Array[MediumCreated] = {
    val bytes = Files.readAllBytes(Paths.get(this.getClass.getClassLoader.getResource(path + "/" + resourceName).toURI))
    val response = server.httpMultipartFormPost("/mediums",
      params = Seq(
        FileElement("file",
          Buf.ByteArray.Owned(bytes),
          Some("image/jpeg"),
          Some(resourceName))
      ),
      headers = headers(accessToken)
    )
    mapper.parse[Array[MediumCreated]](response.contentString)

  }


}
