package io.github.cactacea.backend.server

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.benchmarks.ControllerBenchmark
import com.twitter.finatra.httpclient.RequestBuilder
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.util.{Await, Future}
import io.github.cactacea.backend.auth.core.application.services.AuthenticationService
import io.github.cactacea.backend.auth.enums.AuthType
import io.github.cactacea.backend.auth.server.models.requests.sessions.{PostSignIn, PostSignUp}
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.server.models.requests.session.PostSession
import org.openjdk.jmh.annotations.{Scope, State}

/**
  * ./sbt 'project benchmarks' 'jmh:run ControllerBenchmark'
  */
@State(Scope.Thread)
abstract class BenchmarkHelper extends ControllerBenchmark {

  val mapper: FinatraObjectMapper = injector.instance[FinatraObjectMapper]
  val authenticationService: AuthenticationService = injector.instance[AuthenticationService]

  val sessionUserName: String = s"server_test"
  val sessionPassword: String = s"server_test_password_2000"
  val sessionHeaders: Map[String, String] = createSessionUser()

  def createSessionUser(): Map[String, String] = {

    val signInUpHeaders = Map(Config.auth.headerNames.apiKey -> Config.auth.keys.ios)

    // signUp
    val signUpRequest = RequestBuilder.post(s"/signup")
    val signUpBody = mapper.writePrettyString(PostSignUp(AuthType.username, sessionUserName, sessionPassword, Request()))
    signUpRequest.headers(signInUpHeaders)
    signUpRequest.body(signUpBody)
    Await.result(httpService(signUpRequest).rescue {
      case _: RuntimeException => Future.Unit
    })

    // signIn
    val signInRequest = RequestBuilder.post(s"/signin")
    val signInBody = mapper.writePrettyString(PostSignIn(AuthType.username, sessionUserName, sessionPassword, Request()))
    signInRequest.headers(signInUpHeaders)
    signInRequest.body(signInBody)
    val signInResponse = Await.result(httpService(signInRequest).rescue {
      case e: RuntimeException => Future.exception(e)
    })

    val token = signInResponse.headerMap.getOrNull(Config.auth.headerNames.authorizationKey)

    // registerUser
    val registerUserRequest = RequestBuilder.post(s"/session")
    val registerUserHeaders = Map(
      Config.auth.headerNames.apiKey -> Config.auth.keys.ios,
      Config.auth.headerNames.authorizationKey -> token
    )
    val registerUserBody = mapper.writePrettyString(PostSession(sessionUserName, None))
    registerUserRequest.headers(registerUserHeaders)
    registerUserRequest.body(registerUserBody)
    Await.result(httpService(registerUserRequest).rescue {
      case _: RuntimeException => Future.Unit
    })

    registerUserHeaders
  }

  createSessionUser()

}



