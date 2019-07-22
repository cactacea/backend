package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait GroupsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /groups") (pending)
  test("GET /groups/:id") (pending)
  test("POST /groups") (pending)
  test("PUT /groups/:id") (pending)
  test("POST /groups/:id/join") (pending)
  test("POST /groups/:id/leave") (pending)
  test("GET /groups/:id/accounts") (pending)
  test("DELETE /groups/:id") (pending)
  test("POST /groups/:id/hides") (pending)
  test("DELETE /groups/:id/hides") (pending)
  test("POST /groups/:id/reports") (pending)

}
