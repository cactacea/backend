package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.session.{GetSocialAccountSignIn, PostSocialAccountSignUp}
import io.github.cactacea.backend.models.responses.Authentication
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.{AuthTokenGenerator, SessionContext}
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.responses.CactaceaErrors.{SocialAccountNotFound, SocialAccountNotFoundType}
import io.swagger.models.Swagger


@Singleton
class TwitterController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Social Accounts"
  protected val accountType = "twitter"

  @Inject private var sessionService: SessionsService = _
  @Inject private var tokenGenerator: AuthTokenGenerator = _

  prefix(apiPrefix) {

    postWithDoc(s"/sessions/$accountType") { o =>
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
      ).map({ a =>
        val accessToken = tokenGenerator.generate(a.id.value, request.udid)
        Authentication(a, accessToken)
      })
    }

    getWithPermission(s"/sessions/$accountType")() { o =>
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
      ).map({ a =>
        val accessToken = tokenGenerator.generate(a.id.value, request.udid)
        Authentication(a, accessToken)
      })
    }

  }

}
