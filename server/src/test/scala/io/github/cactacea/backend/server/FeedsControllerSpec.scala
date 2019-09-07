package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finagle.http
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.util.configs.Config

@Singleton
trait FeedsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /feeds") (pending)

  test("POST /feeds") {
    forOne(postFeedGen) { (f) =>
      val headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.ios,
        Config.auth.headerNames.authorizationKey -> sessionToken
      )
      val postFeed = mapper.writePrettyString(f)
      server.httpPost(
        path = "/feeds",
        headers = headers,
        postBody = postFeed,
        andExpect = http.Status.Created
      )
    }

  }

  test("GET /feeds/:id") (pending)
  test("PUT /feeds/:id") (pending)
  test("DELETE /feeds/:id") (pending)
  test("POST /feeds/:id/reports") (pending)

}
