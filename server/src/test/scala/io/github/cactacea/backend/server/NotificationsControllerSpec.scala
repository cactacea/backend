package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait NotificationsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /notifications") (pending)

}
