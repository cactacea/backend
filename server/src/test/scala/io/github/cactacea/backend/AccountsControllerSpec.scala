package io.github.cactacea.backend

import com.google.inject.Singleton
import com.twitter.inject.server.FeatureTest

@Singleton
trait AccountsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /accounts") (pending)
  test("GET /accounts/:id") (pending)
  test("GET /accounts/:id/status") (pending)
  test("PUT /accounts/:id/display_name") (pending)
  test("GET /account/:accountName") (pending)
  test("GET /accounts/:id/feeds") (pending)
  test("GET /accounts/:id/likes") (pending)
  test("GET /accounts/:id/followers") (pending)
  test("GET /accounts/:id/friends") (pending)
  test("POST /accounts/:accountId/groups/:groupId/join") (pending)
  test("POST /accounts/:accountId/groups/:groupId/leave") (pending)
  test("GET /accounts/:id/group") (pending)
  test("GET /accounts/:id/groups") (pending)
  test("POST /accounts/:id/reports") (pending)

}
