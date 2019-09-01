package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FriendRequestsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /users/:id/requests") (pending)
  test("DELETE /users/:id/requests") (pending)

}
