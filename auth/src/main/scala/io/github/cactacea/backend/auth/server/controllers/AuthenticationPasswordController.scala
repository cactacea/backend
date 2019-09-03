package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.PasswordService
import io.github.cactacea.backend.auth.server.models.requests.session.{PostRecover, PostReset}
import io.swagger.models.Swagger

@Singleton
class AuthenticationPasswordController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    passwordService: PasswordService
                                  ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/password/recover") { o =>
      o.summary("Recover password")
        .tag(passwordTag)
        .operationId("recover")
        .request[PostRecover]
        .responseWith(Status.Ok.code, successfulMessage)

    } { request: PostRecover =>
      implicit val r = request.request

      passwordService.recoverPassword(request.email)
    }

    postWithDoc("/password/reset") { o =>
      o.summary("Reset Password")
        .tag(passwordTag)
        .operationId("reset")
        .request[PostReset]
        .responseWith(Status.Ok.code, successfulMessage)

    }  { request: PostReset =>
      implicit val r = request.request

      passwordService.resetPassword(
        request.token,
        request.newPassword)
    }

  }

}
