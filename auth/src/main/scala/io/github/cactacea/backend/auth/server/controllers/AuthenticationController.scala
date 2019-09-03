package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.AuthenticationService
import io.github.cactacea.backend.auth.server.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class AuthenticationController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    authenticationService: AuthenticationService
                                  ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/sessions") { o =>
      o.summary("Sign up")
        .tag(sessionsTag)
        .operationId("signUp")
        .request[PostSignUp]
        .responseWith[User](Status.Ok.code, successfulMessage)
    } { request: PostSignUp =>
      implicit val r = request.request

      authenticationService.signUp(
        request.userName,
        request.password
      )
    }

    getWithDoc("/sessions") { o =>
      o.summary("Sign in")
        .tag(sessionsTag)
        .operationId("signIn")
        .request[GetSignIn]
        .responseWith[User](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason,
        Some(CactaceaErrors(Seq(UserNameOrPasswordNotMatched, UserTerminated))))

    } { request: GetSignIn =>
      implicit val r = request.request

      authenticationService.signIn(
        request.userName,
        request.password
      )
    }

  }

}
