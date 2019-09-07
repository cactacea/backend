package io.github.cactacea.backend.server

import com.twitter.finagle.http
import com.twitter.inject.Test

class FeedsControllerBenchmarkTest extends Test {

  val benchmark = new FeedsControllerBenchmark

  test("createFeed") {
    val result = await(benchmark.createFeed())
    assert(result.statusCode == http.Status.Created.code)
  }


}