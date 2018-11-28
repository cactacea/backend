package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountTerminated, InvalidAccountNameOrPassword}
import io.github.cactacea.backend.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.models.responses.Authentication
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.{AuthTokenGenerator, SessionContext}
import io.swagger.models.Swagger

@Singleton
class SessionsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    sessionService: SessionsService,
                                    tokenGenerator: AuthTokenGenerator
                                  ) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/sessions") { o =>
      o.summary("Sign up")
        .tag(sessionsTag)
        .operationId("signUp")
        .request[PostSignUp]
        .responseWith[Authentication](Status.Ok.code, successfulMessage)
    } { request: PostSignUp =>
      sessionService.signUp(
        request.accountName,
        request.password,
        request.udid,
        request.userAgent,
        SessionContext.deviceType
      ).map({ a =>
        val accessToken = tokenGenerator.generate(a.id.value, request.udid)
        Authentication(a, accessToken)
      })
    }

    getWithPermission("/sessions")() { o =>
      o.summary("Sign in")
        .tag(sessionsTag)
        .operationId("signIn")
        .request[GetSignIn]
        .responseWith[Authentication](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason,
          Some(CactaceaErrors(Seq(InvalidAccountNameOrPassword, AccountTerminated))))

    } { request: GetSignIn =>
      sessionService.signIn(
        request.accountName,
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
