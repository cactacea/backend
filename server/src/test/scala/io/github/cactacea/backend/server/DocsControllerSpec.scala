package io.github.cactacea.backend.server

import java.io.PrintWriter

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.CactaceaBuildInfo

@Singleton
trait DocsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("create swagger.json") {

    val swagger = server.httpGet(s"/docs/model")
    assert(!swagger.contentString.isEmpty)

    val path = CactaceaBuildInfo.baseDirectory.getParent + "/docs/src/main/tut/swagger.json"
    val file = new PrintWriter(path)
    file.write(swagger.contentString)
    file.close()

  }

}
