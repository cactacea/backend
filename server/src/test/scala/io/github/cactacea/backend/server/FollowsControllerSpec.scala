package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FollowsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /users/:id/follows") (pending)
  test("POST /users/:id/follows") (pending)
  test("DELETE /users/:id/follows") (pending)

}
