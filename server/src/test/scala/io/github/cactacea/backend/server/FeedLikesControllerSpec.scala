package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FeedLikesControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /feeds/:id/likes") (pending)
  test("POST /feeds/:id/likes") (pending)
  test("DELETE /feeds/:id/likes") (pending)

}
