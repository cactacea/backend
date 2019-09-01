package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FriendsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("DELETE /users/:id/friends") (pending)

}
