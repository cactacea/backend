package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait MessagesControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /messages") (pending)
  test("POST /messages")(pending)
  test("DELETE /messages")(pending)

}
