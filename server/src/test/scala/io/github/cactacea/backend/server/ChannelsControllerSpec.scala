package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait ChannelsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /channels") (pending)
  test("GET /channels/:id") (pending)
  test("POST /channels") (pending)
  test("PUT /channels/:id") (pending)
  test("POST /channels/:id/join") (pending)
  test("POST /channels/:id/leave") (pending)
  test("GET /channels/:id/accounts") (pending)
  test("DELETE /channels/:id") (pending)
  test("POST /channels/:id/hides") (pending)
  test("DELETE /channels/:id/hides") (pending)
  test("POST /channels/:id/reports") (pending)

}
