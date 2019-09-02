package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finagle.http
import com.twitter.finagle.http.Request
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.server.models.requests.sessions.PostSignUp

@Singleton
trait SessionsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /sessions for Benchmark") {
    forOne(headerGen) { (h) =>
      val userName = "benchmark"
      val password = "benchmark_2020"
      val request = RequestBuilder.get(s"/sessions?userName=${userName}&password=${password}")
      request.headers(h)
      val result = server.httpRequest(request)
      if (result.statusCode == 400) {
        val r = PostSignUp(userName, password, Request())
        val body = mapper.writePrettyString(r)
        server.httpPost(
          path = "/sessions",
          headers = h,
          postBody = body,
          andExpect = http.Status.Ok
        )
      }
    }
  }

  test("POST /sessions") {
    forOne(postSignUpGen, headerGen) { (r, h) =>
      val body = mapper.writePrettyString(r)
      server.httpPost(
        path = "/sessions",
        headers = h,
        postBody = body,
        andExpect = http.Status.Ok
      )
    }
  }

  test("GET  /sessions") (pending)

}
