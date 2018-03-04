package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session.{GetGoogleSignIn, PostGoogleSignUp}
import io.github.cactacea.core.application.services.SessionService
import io.github.cactacea.core.domain.enums.SocialAccountType

@Singleton
class GoogleController extends Controller {

  @Inject var sessionService: SessionService = _

  get("/sessions/google") { request: GetGoogleSignIn =>
    sessionService.signIn(
      SocialAccountType.google,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent
    )
  }

  post("/sessions/google") { request: PostGoogleSignUp =>
    sessionService.signUp(
      SocialAccountType.google,
      request.accountName,
      request.displayName,
      request.password,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.web,
      request.birthday,
      request.location,
      request.bio,
      request.userAgent
    )
  }

}
