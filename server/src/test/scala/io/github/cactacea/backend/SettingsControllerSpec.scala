package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait SettingsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /session/push_notification")  (pending)
  test("PUT /session/push_notification")  (pending)
  test("POST /session/push_token")  (pending)
  test("POST /session/status")  (pending)

}
