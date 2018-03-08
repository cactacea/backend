package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class SessionsController extends Controller {

  @Inject private var sessionService: SessionsService = _

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
      request.userAgent,
      SessionContext.deviceType
    )
  }

  get("/sessions") { request: GetSignIn =>
    sessionService.signIn(
      request.accountName,
      request.password,
      request.udid,
      request.userAgent,
      SessionContext.deviceType
    )
  }

  get("/sessions/:social_account_type") { request: GetSocialAccountSignIn =>
    sessionService.signIn(
      request.socialAccountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent,
      SessionContext.deviceType
    )
  }

  post("/sessions/:social_account_type") { request: PostSocialAccountSignUp =>
    sessionService.signUp(
      request.socialAccountType,
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
      request.userAgent,
      SessionContext.deviceType
    )
  }

}
