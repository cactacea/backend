package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait BlocksControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /session/blocks") (pending)
  test("POST /accounts/:id/blocks") (pending)
  test("DELETE /accounts/:id/blocks") (pending)

}
