package io.github.cactacea.backend.server

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.benchmarks.ControllerBenchmark
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.util.Future
import io.github.cactacea.backend.auth.server.models.requests.session.PutPassword
import org.openjdk.jmh.annotations.Benchmark

class AuthenticationSessionControllerBenchmark extends ControllerBenchmark {

  @Benchmark
  def changePassword(): Future[Response] = {
    val putPassword = PutPassword(sessionPassword, Request())
    val body = mapper.writePrettyString(putPassword)
    val request = RequestBuilder.put(s"/session/password")
    request.headers(sessionHeaders)
    request.body(body)
    httpService(request)
  }

}
