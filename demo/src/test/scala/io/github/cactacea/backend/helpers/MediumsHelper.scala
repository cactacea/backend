package io.github.cactacea.backend.helpers

import java.nio.file.{Files, Paths}

import com.twitter.finagle.http.FileElement
import com.twitter.io.Buf
import io.github.cactacea.backend.DemoServerSpec
import io.github.cactacea.backend.models.responses.MediumCreated

trait MediumsHelper extends CommonHelper {
  self: DemoServerSpec =>

  def uploadMedium(resourceName: String, accessToken: String): Seq[MediumCreated] = {
    val bytes = Files.readAllBytes(Paths.get(this.getClass.getClassLoader.getResource(resourceName).toURI))
    val response = server.httpMultipartFormPost("/mediums",
      params = Seq(
        FileElement("file",
          Buf.ByteArray.Owned(bytes),
          Some("image/jpeg"),
          Some(resourceName))
      ),
      headers = headers(accessToken)
    )
    mapper.parse[Seq[MediumCreated]](response.contentString)

  }


}
