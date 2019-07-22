package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FollowsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /accounts/:id/follows") (pending)
  test("POST /accounts/:id/follows") (pending)
  test("DELETE /accounts/:id/follows") (pending)

}
