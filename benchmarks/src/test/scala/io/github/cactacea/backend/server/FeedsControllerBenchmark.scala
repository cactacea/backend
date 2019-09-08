package io.github.cactacea.backend.server

import com.twitter.finagle.http.Response
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.server.models.requests.feed.PostFeed
import org.openjdk.jmh.annotations.Benchmark

class FeedsControllerBenchmark extends BenchmarkHelper {

  @Benchmark
  def createFeed(): Future[Response] = {
    val postFeed = PostFeed("benchmarks", None, None, FeedPrivacyType.everyone, false, None)
    val body = mapper.writePrettyString(postFeed)
    val request = RequestBuilder.post(s"/feeds")
    request.headers(sessionHeaders)
    request.body(body)
    httpService(request)
  }

}
