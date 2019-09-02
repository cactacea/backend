package io.github.cactacea.backend.server

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.benchmarks.ControllerBenchmark
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.models.requests.sessions.PostSignUp
import org.openjdk.jmh.annotations.Benchmark

class SessionsControllerBenchmark extends ControllerBenchmark {

  override def beforeAll(): Unit = {
    val userName = s"SessionsController"
    val password = s"SessionsController_"
    val signUp = PostSignUp(userName, password, Request())

    val body = mapper.writePrettyString(signUp)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.post("/sessions")
    request.body(body)
    request.headers(headers)
    httpService(request)
  }

  @Benchmark
  def signUp(): Future[Response] = {
    val userName = s"username_${System.nanoTime()}"
    val password = s"password_${System.nanoTime()}"
    val signUp = PostSignUp(userName, password, Request())

    val body = mapper.writePrettyString(signUp)
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.post("/sessions")
    request.body(body)
    request.headers(headers)
    httpService(request)
  }

  @Benchmark
  def signIn(): Future[Response] = {
    val userName = "benchmark"
    val password = "benchmark_2020"
    val headers = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios
    )
    val request = RequestBuilder.get(s"/sessions?userName=${userName}&password=${password}")
    request.headers(headers)
    httpService(request)
  }


}
