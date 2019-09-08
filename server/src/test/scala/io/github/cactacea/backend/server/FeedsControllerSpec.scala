package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finagle.http
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.models.responses.FeedCreated

@Singleton
trait FeedsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /feeds") (pending)

  test("POST PUT GET DELETE /feeds/:id") {
    forOne(postEveryoneFeedGen, putFollowersFeedGen) { (f1, f2) =>
      val headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.ios,
        Config.auth.headerNames.authorizationKey -> sessionToken
      )
      // POST a feed
      val postFeed = mapper.writePrettyString(f1)
      val feed = server.httpPostJson[FeedCreated](
        path = "/feeds",
        headers = headers,
        postBody = postFeed,
        andExpect = http.Status.Created
      )
      // PUT a feed
      val putFeed = mapper.writePrettyString(f2)
      server.httpPut(
        path = s"/feeds/${feed.id}",
        headers = headers,
        putBody = putFeed,
        andExpect = http.Status.Ok
      )
      // GET a feed
      val result1 = server.httpGetJson[Feed](path = s"/feeds/${feed.id}", headers = headers, andExpect = http.Status.Ok)
      assert(result1.id == feed.id)
      assert(result1.message == f2.message)
      assert(result1.warning == f2.contentWarning)
      assert(result1.commentCount == 0)
      assert(result1.likeCount == 0)
      assert(!result1.liked)
      // DELETE a feed
      server.httpDelete(path = s"/feeds/${feed.id}", headers = headers)
      // GET a feed
      server.httpGet(path = s"/feeds/${feed.id}", headers = headers, andExpect = http.Status.NotFound)
    }
  }

  test("POST /feeds/:id/reports") (pending)

}
