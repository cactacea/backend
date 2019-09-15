package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait UsersControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /users") (pending)
  test("GET /users/:id") (pending)
  test("GET /users/:id/status") (pending)
  test("PUT /users/:id/display_name") (pending)
  test("GET /user/:userName") (pending)
  test("GET /users/:id/tweets") (pending)
  test("GET /users/:id/likes") (pending)
  test("GET /users/:id/followers") (pending)
  test("GET /users/:id/friends") (pending)
  test("POST /users/:userId/channels/:channelId/join") (pending)
  test("POST /users/:userId/channels/:channelId/leave") (pending)
  test("GET /users/:id/channel") (pending)
  test("GET /users/:id/channels") (pending)
  test("POST /users/:id/reports") (pending)

}
