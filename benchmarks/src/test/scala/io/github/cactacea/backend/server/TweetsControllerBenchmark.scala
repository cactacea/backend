package io.github.cactacea.backend.server

import com.twitter.finagle.http.Response
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.TweetPrivacyType
import io.github.cactacea.backend.server.models.requests.tweet.PostTweet
import org.openjdk.jmh.annotations.Benchmark

class TweetsControllerBenchmark extends BenchmarkHelper {

  @Benchmark
  def createTweet(): Future[Response] = {
    val postTweet = PostTweet("benchmarks", None, None, TweetPrivacyType.everyone, false, None)
    val body = mapper.writePrettyString(postTweet)
    val request = RequestBuilder.post(s"/tweets")
    request.headers(sessionHeaders)
    request.body(body)
    httpService(request)
  }

}
