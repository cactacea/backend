package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.{AuthenticationService, EmailAuthenticationService}
import io.github.cactacea.backend.auth.core.domain.models.Session
import io.github.cactacea.backend.auth.enums.AuthType
import io.github.cactacea.backend.auth.server.models.requests.sessions.{PostSignIn, PostSignUp}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class AuthenticationController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    authenticationService: AuthenticationService,
                                    emailAuthenticationService: EmailAuthenticationService
                                  ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/signup") { o =>
      o.summary("Sign up")
        .tag(sessionsTag)
        .operationId("signUp")
        .request[PostSignUp]
        .responseWith[Session](Status.Ok.code, successfulMessage)
    } { request: PostSignUp =>
      implicit val r = request.request

      request.authType match {
        case AuthType.username =>
          authenticationService.signUp(
            request.identifier,
            request.password
          )
        case AuthType.email =>
          emailAuthenticationService.signUp(
            request.identifier,
            request.password
          )
      }
    }

    postWithDoc("/signin") { o =>
      o.summary("Sign in")
        .tag(sessionsTag)
        .operationId("signIn")
        .request[PostSignIn]
        .responseWith[Session](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason,
        Some(CactaceaErrors(Seq(UserNameOrPasswordNotMatched, UserTerminated))))

    } { request: PostSignIn =>
      implicit val r = request.request

      request.authType match {
        case AuthType.username =>
          authenticationService.signIn(
            request.identifier,
            request.password
          )
        case AuthType.email =>
          emailAuthenticationService.signIn(
            request.identifier,
            request.password
          )
      }

    }

  }

}
