package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FeedsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /feeds") (pending)
  test("POST /feeds") (pending)
  test("GET /feeds/:id") (pending)
  test("PUT /feeds/:id") (pending)
  test("DELETE /feeds/:id") (pending)
  test("POST /feeds/:id/reports") (pending)

}
