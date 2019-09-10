package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.PasswordService
import io.github.cactacea.backend.auth.server.models.requests.sessions.{PostRecoverPassword, PostResetPassword}
import io.swagger.models.Swagger

@Singleton
class PasswordController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    passwordService: PasswordService
                                  ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/password/recover") { o =>
      o.summary("Recover password")
        .tag(sessionsTag)
        .operationId("recoverPassword")
        .request[PostRecoverPassword]
        .responseWith(Status.Ok.code, successfulMessage)

    } { request: PostRecoverPassword =>
      implicit val r = request.request

      passwordService.recoverPassword(request.email)
    }

    postWithDoc("/password/reset") { o =>
      o.summary("Reset Password")
        .tag(sessionsTag)
        .operationId("resetPassword")
        .request[PostResetPassword]
        .responseWith(Status.Ok.code, successfulMessage)

    }  { request: PostResetPassword =>
      implicit val r = request.request

      passwordService.resetPassword(
        request.token,
        request.newPassword)
    }

  }

}
