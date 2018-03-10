package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class SocialAccountsController @Inject()(s: Swagger) extends Controller with SwaggerController {

  implicit protected val swagger = s

  private val tagName = "Social Accounts"

  @Inject private var sessionService: SessionsService = _

  postWithDoc("/sessions/:account_type") { o =>
    o.summary("Social account sign up")
      .tag(tagName)
      .routeParam[String]("account_type", "Social account type. (facebook or google or twitter)")
      .bodyParam[PostSignUp]("PostSocialAccountSignUp", "sign in information")
      .responseWith[Account](Status.Ok.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred.")
      .responseWith[Array[NotFound]](Status.NotFound.code, "social account not found.")

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
    o.summary("Social account sign in")
      .tag(tagName)
      .routeParam[String]("account_type", "Social account type. (facebook or google or twitter)")
      .bodyParam[GetSocialAccountSignIn]("GetSocialAccountSignIn", "sign in information")
      .responseWith[Account](Status.Ok.code, "successful operation")
      .responseWith[Array[BadRequest]](Status.BadRequest.code, "validation error occurred")
      .responseWith[Array[NotFound]](Status.NotFound.code, "social account not found")

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
