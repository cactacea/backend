package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait SessionControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /session") (pending)
  test("DELETE /session") (pending)
  test("PUT /session/account_name") (pending)
  test("PUT /session/password") (pending)
  test("PUT /session/profile") (pending)
  test("POST /session/profile_image") (pending)
  test("DELETE /session/profile_image") (pending)
  test("GET /session/feeds") (pending)
  test("GET /session/likes") (pending)
  test("GET /session/follows") (pending)
  test("GET /session/followers") (pending)
  test("GET /session/friends") (pending)
  test("GET /session/channels") (pending)
  test("GET /session/hides") (pending)
  test("GET /session/invitations") (pending)
  test("GET /session/mutes") (pending)
  test("GET /session/requests") (pending)
  test("POST /session/requests/:id/accept") (pending)
  test("POST /session/requests/:id/reject") (pending)

}

