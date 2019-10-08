package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finagle.http
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.domain.models.Tweet
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.models.responses.TweetCreated

@Singleton
trait TweetsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("GET /tweets") (pending)

  test("POST PUT GET DELETE /tweets/:id") {
    forOne(postEveryoneTweetGen, putFollowersTweetGen) { (f1, f2) =>
      val headers = Map(
        Config.auth.headerNames.apiKey -> Config.auth.keys.ios,
        Config.auth.headerNames.authorizationKey -> sessionToken
      )
      // POST a tweet
      val postTweet = mapper.writePrettyString(f1)
      val tweet = server.httpPostJson[TweetCreated](
        path = "/tweets",
        headers = headers,
        postBody = postTweet,
        andExpect = http.Status.Created
      )
      // PUT a tweet
      val putTweet = mapper.writePrettyString(f2)
      server.httpPut(
        path = s"/tweets/${tweet.id}",
        headers = headers,
        putBody = putTweet,
        andExpect = http.Status.Ok
      )
      // GET a tweet
      val result1 = server.httpGetJson[Tweet](path = s"/tweets/${tweet.id}", headers = headers, andExpect = http.Status.Ok)
      assert(result1.id == tweet.id)
      assert(result1.message == f2.message)
      assert(result1.warning == f2.contentWarning)
      assert(result1.commentCount == 0)
      assert(result1.likeCount == 0)
      assert(!result1.liked)
      // DELETE a tweet
      server.httpDelete(path = s"/tweets/${tweet.id}", headers = headers)
      // GET a tweet
      server.httpGet(path = s"/tweets/${tweet.id}", headers = headers, andExpect = http.Status.NotFound)
    }
  }

  test("POST /tweets/:id/reports") (pending)

}
