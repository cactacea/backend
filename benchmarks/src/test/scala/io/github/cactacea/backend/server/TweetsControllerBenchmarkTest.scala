package io.github.cactacea.backend.server

import com.twitter.finagle.http
import com.twitter.inject.Test

class TweetsControllerBenchmarkTest extends Test {

  val benchmark = new TweetsControllerBenchmark

  test("createTweet") {
    val result = await(benchmark.createTweet())
    assert(result.statusCode == http.Status.Created.code)
  }


}