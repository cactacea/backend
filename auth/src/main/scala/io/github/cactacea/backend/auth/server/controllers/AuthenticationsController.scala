package io.github.cactacea.backend.auth.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.core.application.services.{AuthenticationService, EmailAuthenticationService, SocialAuthenticationService}
import io.github.cactacea.backend.auth.core.domain.models.SessionToken
import io.github.cactacea.backend.auth.enums.AuthType
import io.github.cactacea.backend.auth.server.models.requests.sessions.{PostRejectToken, PostSignIn, PostSignUp, PostVerifyToken}
import io.github.cactacea.backend.auth.server.models.requests.social.PostSocialSignup
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class AuthenticationsController@Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    authenticationService: AuthenticationService,
                                    emailAuthenticationService: EmailAuthenticationService,
                                    socialAuthenticationService: SocialAuthenticationService
                                  ) extends BaseController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/signup") { o =>
      o.summary("Sign up")
        .tag(sessionsTag)
        .operationId("signUp")
        .request[PostSignUp]
        .responseWith[SessionToken](Status.Ok.code, successfulMessage)
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

    postWithDoc("/signup/:provider") { o =>
      o.summary("Sign up")
        .tag(sessionsTag)
        .operationId("socialSignUp")
        .request[PostSocialSignup]
        .responseWith[SessionToken](Status.Ok.code, successfulMessage)
    } { request: PostSocialSignup =>
      implicit val r = request.request
      socialAuthenticationService.authenticate(
        request.provider,
        request.token,
        request.expiresIn,
        request.secret
      )
    }

    postWithDoc("/signin") { o =>
      o.summary("Sign in")
        .tag(sessionsTag)
        .operationId("signIn")
        .request[PostSignIn]
        .responseWith[SessionToken](Status.Ok.code, successfulMessage)
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

    postWithDoc("/verify") { o =>
      o.summary("Verify token")
        .tag(sessionsTag)
        .operationId("verifyEmail")
        .request[PostVerifyToken]
        .responseWith(Status.Ok.code, successfulMessage)

    } { req: PostVerifyToken =>
      emailAuthenticationService.verify(req.token).map(_ => response.ok)
    }

    postWithDoc("/reject") { o =>
      o.summary("Reject token")
        .tag(sessionsTag)
        .operationId("rejectEmail")
        .request[PostRejectToken]
        .responseWith(Status.Ok.code, successfulMessage)

    } { req: PostVerifyToken =>
      emailAuthenticationService.verify(req.token).map(_ => response.ok)
    }

  }

}
