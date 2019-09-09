package io.github.cactacea.backend.server

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.enums.AuthType
import io.github.cactacea.backend.auth.server.models.requests.sessions.PostSignUp
import io.github.cactacea.backend.core.util.configs.Config
import org.openjdk.jmh.annotations.Benchmark

class AuthenticationControllerBenchmark extends BenchmarkHelper {

  @Benchmark
  def signUp(): Future[Response] = {
    val userName = s"username_${System.nanoTime()}"
    val password = s"password_${System.nanoTime()}"
    val signUp = PostSignUp(AuthType.username, userName, password, Request())
    val signUpBody = mapper.writePrettyString(signUp)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.post(s"/signup")
    request.body(signUpBody)
    request.headers(headers)
    httpService(request)
  }

  @Benchmark
  def signIn(): Future[Response] = {
    val signIn = PostSignUp(AuthType.username, sessionUserName, sessionPassword, Request())
    val signInBody = mapper.writePrettyString(signIn)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.post(s"/signin")
    request.headers(headers)
    request.body(signInBody)
    httpService(request)
  }


}
