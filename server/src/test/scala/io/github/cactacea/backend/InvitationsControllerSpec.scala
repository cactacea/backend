package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait InvitationsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /invitations/:id/accept") (pending)
  test("POST /invitations/:id/reject") (pending)
  test("POST /groups/:id/invitations") (pending)
  test("POST /accounts/:accountId/groups/:groupId/invitations") (pending)

}
