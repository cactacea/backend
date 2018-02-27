package io.github.cactacea.core.infrastructure.clients.s3

import java.io.File
import java.nio.file.Files

import com.twitter.finagle.http.MediaType
import com.twitter.util.Await
import io.github.cactacea.core.helpers.DAOSpec

class S3HttpClientSpec extends DAOSpec {

  var s3HttpClient = injector.instance[S3HttpClient]

  test("upload a image file") {

    val url = getClass.getResource("/cactacea.gif")
    val file = new File(url.toURI)
    val bytes = Files.readAllBytes(file.toPath)
    val result = Await.result(s3HttpClient.put(Some(MediaType.Gif), bytes))
    assert(result._2 != "")

  }

}
