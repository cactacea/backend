package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait InvitationsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /invitations/:id/accept") (pending)
  test("POST /invitations/:id/reject") (pending)
  test("POST /channels/:id/invitations") (pending)
  test("POST /users/:userId/channels/:channelId/invitations") (pending)

}
