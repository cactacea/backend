package io.github.cactacea.backend.server

import com.twitter.finagle.http
import com.twitter.inject.Test

class AuthenticationControllerBenchmarkTest extends Test {

  val benchmark = new AuthenticationControllerBenchmark

  test("signUp") {
    val result = await(benchmark.signUp())
    assert(result.statusCode == http.Status.Ok.code)
  }

  test("signIn") {
    val result = await(benchmark.signIn())
    assert(result.statusCode == http.Status.Ok.code)
  }
}