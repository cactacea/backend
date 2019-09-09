package io.github.cactacea.backend.server

import com.google.inject.Singleton
import com.twitter.finagle.http
import com.twitter.inject.server.FeatureTest

@Singleton
trait SessionsControllerSpec extends FeatureTest {
  self: CactaceaServerSpec =>

  test("POST /signup") {
    forOne(postSignUpGen, headerGen) { (r, h) =>
      val body = mapper.writePrettyString(r)
      server.httpPost(
        path = s"/signup",
        headers = h,
        postBody = body,
        andExpect = http.Status.Ok
      )
    }
  }

  test("GET  /signin") (pending)

}
