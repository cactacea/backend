package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait CommentLikesControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /comments/:id/likes") (pending)
  test("POST /comments/:id/likes") (pending)
  test("DELETE /comments/:id/likes") (pending)

}
