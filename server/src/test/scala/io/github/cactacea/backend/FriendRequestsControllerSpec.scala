package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FriendRequestsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /accounts/:id/requests") (pending)
  test("DELETE /accounts/:id/requests") (pending)

}
