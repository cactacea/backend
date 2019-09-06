package io.github.cactacea.backend.server

import com.twitter.inject.Test

class AuthenticationSessionControllerBenchmarkTest extends Test {

  val benchmark = new AuthenticationSessionControllerBenchmark

  test("changePassword") {
    val result = await(benchmark.changePassword())
    assert(result.statusCode == 200)
  }


}