package io.github.cactacea.backend.utils.clients.s3

//import java.io.File
//import java.nio.file.Files
//
//import com.twitter.finagle.http.MediaType
//import com.twitter.util.Await
//
//class S3HttpClientSpec extends IntegrationTest {
//
//  var s3HttpClient = injector.instance[S3HttpClient]
//
//  test("upload a image file") {
//
//    val url = getClass.getResource("/cactacea.gif")
//    val file = new File(url.toURI)
//    val bytes = Files.readAllBytes(file.toPath)
//    val result = Await.result(s3HttpClient.put(Some(MediaType.Gif), bytes))
//    assert(result._2 != "")
//
//  }
//
//}
