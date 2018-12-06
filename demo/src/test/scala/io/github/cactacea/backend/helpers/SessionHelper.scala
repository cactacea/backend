package io.github.cactacea.backend.helpers

import java.nio.file.{Files, Paths}

import com.twitter.finagle.http.{FileElement, Response}
import com.twitter.io.Buf
import io.github.cactacea.backend.DemoServerSpec

trait SessionHelper extends CommonHelper {
  self: DemoServerSpec =>

  def signOut(accessToken: String): Response = {
    server.httpDelete(
      path = "/session",
      headers = headers(accessToken)
    )
  }

  def updateProfileImage(resourceName: String, accessToken: String): Response = {
    val bytes = Files.readAllBytes(Paths.get(this.getClass.getClassLoader.getResource(resourceName).toURI))
    server.httpMultipartFormPost("/session/profile_image",
      params = Seq(
        FileElement("file",
          Buf.ByteArray.Owned(bytes),
          Some("image/jpeg"),
          Some(resourceName))
      ),
      headers = headers(accessToken)
    )
  }

}
