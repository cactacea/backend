package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger


@Singleton
class GoogleController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Social Accounts"
  protected val accountType = "google"

  @Inject private var sessionService: SessionsService = _

  postWithDoc(c.rootPath + s"/sessions/$accountType") { o =>
    o.summary(s"Sign up by $accountType")
      .tag(tagName)
      .request[PostSocialAccountSignUp]
      .responseWith[Account](Status.Ok.code, successfulMessage)

      .responseWith[Array[SocialAccountNotFoundType]](SocialAccountNotFound.status.code, SocialAccountNotFound.message)

  } { request: PostSocialAccountSignUp =>
    sessionService.signUp(
      accountType,
      request.name,
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

  getWithDoc(c.rootPath + s"/sessions/$accountType") { o =>
    o.summary(s"Sign in by $accountType")
      .tag(tagName)
      .request[GetSocialAccountSignIn]
      .responseWith[Account](Status.Ok.code, successfulMessage)

      .responseWith[Array[SocialAccountNotFoundType]](SocialAccountNotFound.status.code, SocialAccountNotFound.message)

  } { request: GetSocialAccountSignIn =>
    sessionService.signIn(
      accountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.udid,
      request.userAgent,
      SessionContext.deviceType
    )
  }

}
