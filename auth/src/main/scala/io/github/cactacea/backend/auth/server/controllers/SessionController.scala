package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.{AuthenticationService, PasswordService}
import io.github.cactacea.backend.auth.server.models.requests.session.{PutPassword, PutUserName}
import io.github.cactacea.backend.auth.server.utils.contexts.AuthenticationContext
import io.github.cactacea.backend.auth.server.utils.filters.AuthenticationFilter
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class SessionController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    authenticationService: AuthenticationService,
                                    passwordService: PasswordService
                                               ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    filter[AuthenticationFilter].putWithDoc("/session/username") { o =>
      o.summary("Update the user name")
        .tag(sessionTag)
        .operationId("updateUserName")
        .request[PutUserName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserAlreadyExist))))
    } { request: PutUserName =>
      authenticationService.changeUserName(
        AuthenticationContext.auth.providerId,
        AuthenticationContext.auth.providerKey,
        request.name,
      ).map(_ => response.ok)
    }

    filter[AuthenticationFilter].putWithDoc("/session/password") { o =>
      o.summary("Update the password")
        .tag(sessionTag)
        .operationId("updatePassword")
        .request[PutPassword]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutPassword =>
      implicit val r = request.request

      passwordService.changePassword(
        AuthenticationContext.auth.providerId,
        AuthenticationContext.auth.providerKey,
        request.newPassword
      ).map(_ => response.ok)
    }

  }

}
