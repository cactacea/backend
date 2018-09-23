package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.sessions.{GetAuthenticationCode, GetSocialAccountSignIn, PostSocialAccountSignUp}
import io.github.cactacea.backend.models.responses.Authentication
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.{AuthTokenGenerator, SessionContext}
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{SocialAccountNotFound}
import io.swagger.models.Swagger

@Singleton
class SocialAccountsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  protected val tagName = "Social Accounts"

  @Inject private var sessionService: SessionsService = _
  @Inject private var tokenGenerator: AuthTokenGenerator = _

  prefix(apiPrefix) {

    postWithPermission(s"/sessions/social_accounts")() { o =>
      o.summary(s"Sign up by social accounts")
        .tag(tagName)
        .request[PostSocialAccountSignUp]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[Array[SocialAccountNotFound.type]](SocialAccountNotFound.status.code, SocialAccountNotFound.message)

    } { request: PostSocialAccountSignUp =>
      sessionService.signUp(
        request.providerId,
        request.name,
        request.displayName,
        request.password,
        request.providerKey,
        request.authenticationCode,
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

    getWithPermission(s"/sessions/social_accounts")() { o =>
      o.summary(s"Sign in by social accounts")
        .tag(tagName)
        .request[GetSocialAccountSignIn]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[Array[SocialAccountNotFound.type]](SocialAccountNotFound.status.code, SocialAccountNotFound.message)

    } { request: GetSocialAccountSignIn =>
      sessionService.signIn(
        request.providerId,
        request.providerKey,
        request.authenticationCode,
        request.udid,
        request.userAgent,
        SessionContext.deviceType
      ).map({ a =>
        val accessToken = tokenGenerator.generate(a.id.value, request.udid)
        Authentication(a, accessToken)
      })
    }

    getWithPermission(s"/sessions/social_accounts/issue_code")() { o =>
      o.summary(s"Issue authentication code.")
        .tag(tagName)
        .request[GetAuthenticationCode]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[Array[SocialAccountNotFound.type]](SocialAccountNotFound.status.code, SocialAccountNotFound.message)

    } { request: GetAuthenticationCode =>
      sessionService.issueAuthenticationCode(
        request.providerId,
        request.providerKey
      ).map( _ => response.ok )
    }

  }

}

