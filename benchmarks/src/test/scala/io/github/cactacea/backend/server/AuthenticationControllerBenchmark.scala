package io.github.cactacea.backend.server

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.server.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.core.util.configs.Config
import org.openjdk.jmh.annotations.Benchmark

class AuthenticationControllerBenchmark extends BenchmarkHelper {

  @Benchmark
  def signUp(): Future[Response] = {
    val userName = s"username_${System.nanoTime()}"
    val password = s"password_${System.nanoTime()}"
    val signUp = PostSignUp(userName, password, Request())
    val signUpBody = mapper.writePrettyString(signUp)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.post("/sessions")
    request.body(signUpBody)
    request.headers(headers)
    httpService(request)
  }

  @Benchmark
  def signIn(): Future[Response] = {
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val signIn = GetSignIn(sessionUserName, sessionPassword, Request())
    val signInBody = mapper.writePrettyString(signIn)
    val request = RequestBuilder.get(s"/sessions")
    request.body(signInBody)
    request.headers(headers)
    httpService(request)
  }


}
