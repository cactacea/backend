package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.enums.SocialAccountType

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

  get("/sessions/google") { request: GetGoogleSignIn =>
    sessionService.signIn(
      SocialAccountType.google,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
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

  get("/sessions/facebook") { request: GetFacebookSignIn =>
    sessionService.signIn(
      SocialAccountType.facebook,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent
    )
  }

}
