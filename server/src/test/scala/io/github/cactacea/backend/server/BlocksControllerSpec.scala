package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait BlocksControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /session/blocks") (pending)
  test("POST /users/:id/blocks") (pending)
  test("DELETE /users/:id/blocks") (pending)

}
