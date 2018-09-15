package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.models.responses.Authentication
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.utils.auth.{AuthTokenGenerator, SessionContext}
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountTerminated, AccountTerminatedType, InvalidAccountNameOrPassword, InvalidAccountNameOrPasswordType}
import io.swagger.models.Swagger

@Singleton
class SessionsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  @Inject private var sessionService: SessionsService = _
  @Inject private var tokenGenerator: AuthTokenGenerator = _

  protected val tagName = "Sessions"

  prefix(apiPrefix) {

    postWithDoc("/sessions") { o =>
      o.summary("Sign up")
        .tag(tagName)
        .request[PostSignUp]
        .responseWith[Authentication](Status.Ok.code, successfulMessage)

    } { request: PostSignUp =>
      sessionService.signUp(
        request.name,
        request.displayName,
        request.password,
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

    getWithPermission("/sessions")() { o =>
      o.summary("Sign in")
        .tag(tagName)
        .request[GetSignIn]
        .responseWith[Authentication](Status.Ok.code, successfulMessage)
        .responseWith[Array[InvalidAccountNameOrPasswordType]](InvalidAccountNameOrPassword.status.code, InvalidAccountNameOrPassword.message)
        .responseWith[Array[AccountTerminatedType]](AccountTerminated.status.code, AccountTerminated.message)

    } { request: GetSignIn =>
      sessionService.signIn(
        request.name,
        request.password,
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
