package io.github.cactacea.backend.server

import com.twitter.inject.Test

class AuthenticationControllerBenchmarkTest extends Test {

  val benchmark = new AuthenticationControllerBenchmark

  test("signUp") {
    val result = await(benchmark.signUp())
    assert(result.statusCode == 200)
  }

  test("signIn") {
    val result = await(benchmark.signIn())
    assert(result.statusCode == 200)
  }
}