package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountTerminated, InvalidAccountNameOrPassword}
import io.github.cactacea.backend.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.github.cactacea.backend.utils.services.AuthenticationsService
import io.swagger.models.Swagger

@Singleton
class SessionsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    sessionsService: AuthenticationsService

                                  ) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/sessions") { o =>
      o.summary("Sign up")
        .tag(sessionsTag)
        .operationId("signUp")
        .request[PostSignUp]
        .responseWith[Account](Status.Ok.code, successfulMessage)
    } { request: PostSignUp =>
      implicit val r = request.request

      sessionsService.signUp(
        request.accountName,
        request.password,
        request.udid,
        request.userAgent,
        CactaceaContext.deviceType
      )
    }

    getWithDoc("/sessions") { o =>
      o.summary("Sign in")
        .tag(sessionsTag)
        .operationId("signIn")
        .request[GetSignIn]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason,
          Some(CactaceaErrors(Seq(InvalidAccountNameOrPassword, AccountTerminated))))

    } { request: GetSignIn =>
      implicit val r = request.request
      sessionsService.signIn(
        request.accountName,
        request.password,
        request.udid,
        request.userAgent,
        CactaceaContext.deviceType
      )
    }

  }

}
