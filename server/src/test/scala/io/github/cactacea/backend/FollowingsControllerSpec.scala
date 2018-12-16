package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FollowingsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /accounts/:id/following") (pending)
  test("POST /accounts/:id/following") (pending)
  test("DELETE /accounts/:id/following") (pending)

}
