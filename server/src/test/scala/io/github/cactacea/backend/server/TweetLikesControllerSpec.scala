package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait TweetLikesControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /tweets/:id/likes") (pending)
  test("POST /tweets/:id/likes") (pending)
  test("DELETE /tweets/:id/likes") (pending)

}
