package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session.{GetTwitterSignIn, PostTwitterSignUp}
import io.github.cactacea.core.application.services.SessionService
import io.github.cactacea.core.domain.enums.SocialAccountType

@Singleton
class TwitterController extends Controller {

  @Inject var sessionService: SessionService = _

  post("/sessions/twitter") { request: PostTwitterSignUp =>
    sessionService.signUp(
      SocialAccountType.twitter,
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

  get("/sessions/twitter") { request: GetTwitterSignIn =>
    sessionService.signIn(
      SocialAccountType.twitter,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent
    )
  }

}
