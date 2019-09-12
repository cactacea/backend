package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.{AuthenticationService, PasswordService, SocialAuthenticationService}
import io.github.cactacea.backend.auth.server.models.requests.session.{PostSession, PutPassword, PutUserName}
import io.github.cactacea.backend.auth.server.models.requests.social.PostSocialLink
import io.github.cactacea.backend.auth.server.utils.contexts.AuthenticationContext
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class AuthenticationController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    authenticationService: AuthenticationService,
                                    passwordService: PasswordService,
                                    socialAuthenticationService: SocialAuthenticationService
                                               ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/session") { o =>
      o.summary("Register user")
        .tag(sessionTag)
        .operationId("registerSession")
        .request[PostSession]
        .responseWith[User](Status.Ok.code, successfulMessage)
    } { request: PostSession =>
      authenticationService.create(
        AuthenticationContext.auth.providerId,
        AuthenticationContext.auth.providerKey,
        request.userName,
        request.displayName
      )
    }

    putWithDoc("/session/username") { o =>
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

    putWithDoc("/session/password") { o =>
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

    post("/session/:provider/link") { request: PostSocialLink =>
      socialAuthenticationService.link(
        request.provider,
        request.token,
        request.expiresIn,
        request.secret,
        AuthenticationContext.auth.providerId,
        AuthenticationContext.auth.providerKey
      )
    }

  }

}
