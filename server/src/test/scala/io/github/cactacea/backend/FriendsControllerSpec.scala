package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait FriendsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("DELETE /accounts/:id/friends") (pending)

}
