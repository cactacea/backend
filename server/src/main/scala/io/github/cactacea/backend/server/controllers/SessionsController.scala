package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.application.services.AuthenticationService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserTerminated, UserNameOrPasswordNotMatched}
import io.github.cactacea.backend.server.models.requests.sessions.{GetSignIn, PostSignUp}
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class SessionsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    userAuthenticationService: AuthenticationService
                                  ) extends CactaceaController {

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

      userAuthenticationService.signUp(
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
      userAuthenticationService.signIn(
        request.userName,
        request.password
      )
    }

  }

}
