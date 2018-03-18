package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors.{AccountTerminated, InvalidAccountNameOrPassword, _}
import io.swagger.models.Swagger

@Singleton
class SessionsController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Sessions"

  @Inject private var sessionService: SessionsService = _

  postWithDoc(c.rootPath + "/sessions") { o =>
    o.summary("Sign up")
      .tag(tagName)
      .request[PostSignUp]
      .responseWith[Account](Status.Ok.code, successfulMessage)


  } { request: PostSignUp =>
    sessionService.signUp(
      request.name,
      request.displayName,
      request.password,
      request.udid,
      request.web,
      request.birthday,
      request.location,
      request.bio,
      request.userAgent,
      SessionContext.deviceType
    )
  }

  getWithDoc(c.rootPath + "/sessions") { o =>
    o.summary("Sign in")
      .tag(tagName)
      .request[GetSignIn]
      .responseWith[Account](Status.Ok.code, successfulMessage)

      .responseWith[Array[InvalidAccountNameOrPasswordType]](InvalidAccountNameOrPassword.status.code, InvalidAccountNameOrPassword.message)
      .responseWith[Array[AccountTerminatedType]](AccountTerminated.status.code, AccountTerminated.message)

  } { request: GetSignIn =>
    sessionService.signIn(
      request.name,
      request.password,
      request.udid,
      request.userAgent,
      SessionContext.deviceType
    )
  }

}
