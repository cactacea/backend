package io.github.cactacea.core.util

import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class CactaceaServerSpec extends FeatureTest {

  override val server = new EmbeddedHttpServer(
    twitterServer = new CactaceaServer
  )

  override protected def beforeAll(): Unit = {
    super.beforeAll()
  }

  override protected def afterAll() = {
    super.afterAll()
  }

}
