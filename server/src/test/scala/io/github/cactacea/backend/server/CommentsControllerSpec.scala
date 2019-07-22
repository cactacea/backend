package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait CommentsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /comments") (pending)
  test("POST /comments") (pending)
  test("GET /comments/:id") (pending)
  test("DELETE /comments/:id") (pending)
  test("POST /comments/:id/reports") (pending)

}
