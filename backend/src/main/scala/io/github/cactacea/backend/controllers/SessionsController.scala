package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._

@Singleton
class SessionsController extends Controller {

  @Inject var sessionService: SessionService = _

  post("/sessions") { request: PostSignUp =>
    sessionService.signUp(
      request.accountName,
      request.displayName,
      request.password,
      request.udid,
      request.web,
      request.birthday,
      request.location,
      request.bio,
      request.userAgent
    )
  }

  get("/sessions") { request: GetSignIn =>
    sessionService.signIn(
      request.accountName,
      request.password,
      request.udid,
      request.userAgent
    )
  }

}
