package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait MutesControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /users/:id/mutes") (pending)
  test("DELETE /users/:id/mutes") (pending)

}
