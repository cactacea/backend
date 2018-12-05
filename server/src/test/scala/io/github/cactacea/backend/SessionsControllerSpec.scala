package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait SessionsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /sessions") (pending)
  test("GET  /sessions") (pending)

}
