package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.SocialAccountNotFound
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger


@Singleton
class SocialAccountsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Social Accounts"

  @Inject private var sessionService: SessionsService = _

  postWithDoc("/sessions/:account_type") { o =>
    o.summary("Sign up by social account")
      .tag(tagName)
      .request[PostSocialAccountSignUp]
      .responseWith[Account](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[NotFound]](Status.NotFound.code, SocialAccountNotFound.message)

  } { request: PostSocialAccountSignUp =>
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

  getWithDoc("/sessions/:account_type") { o =>
    o.summary("Sign in by via social account")
      .tag(tagName)
      .request[GetSocialAccountSignIn]
      .responseWith[Account](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[NotFound]](Status.NotFound.code, SocialAccountNotFound.message)

  } { request: GetSocialAccountSignIn =>
    sessionService.signIn(
      request.socialAccountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent,
      SessionContext.deviceType
    )
  }

}