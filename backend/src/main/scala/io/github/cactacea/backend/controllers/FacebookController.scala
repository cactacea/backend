package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session.{GetFacebookSignIn, PostFacebookSignUp}
import io.github.cactacea.core.application.services.SessionsService
import io.github.cactacea.core.domain.enums.SocialAccountType

@Singleton
class FacebookController extends Controller {

  @Inject var sessionService: SessionsService = _

  get("/sessions/facebook") { request: GetFacebookSignIn =>
    sessionService.signIn(
      SocialAccountType.facebook,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent
    )
  }

  post("/sessions/facebook") { request: PostFacebookSignUp =>
    sessionService.signUp(
      SocialAccountType.facebook,
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
