package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.BadRequest
import io.swagger.models.Swagger

@Singleton
class SessionsController @Inject()(s: Swagger) extends Controller with SwaggerController {

  implicit protected val swagger = s

  private val tagName = "Sessions"

  @Inject private var sessionService: SessionsService = _

  postWithDoc("/sessions") { o =>
    o.summary("Sign up")
      .tag(tagName)
      .bodyParam[PostSignUp]("PostSignUp", "sign up information")
      .responseWith[Account](Status.Ok.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")

  } { request: PostSignUp =>
    sessionService.signUp(
      request.accountName,
      request.displayName,
      request.password,
      request.udid,
      request.web,
      request.birthday,
      request.location,
      request.bio,
      request.userAgent,
      SessionContext.deviceType
    )
  }

  getWithDoc("/sessions") { o =>
    o.summary("Sign in")
      .tag(tagName)
      .bodyParam[GetSignIn]("GetSignIn", "sign in information")
      .responseWith[Account](Status.Ok.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "invalid Account name or password")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "account terminated")

  } { request: GetSignIn =>
    sessionService.signIn(
      request.accountName,
      request.password,
      request.udid,
      request.userAgent,
      SessionContext.deviceType
    )
  }

}
