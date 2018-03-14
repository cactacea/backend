package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import io.github.cactacea.backend.models.requests.setting.PostSocialAccount
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.BadRequest
import io.swagger.models.Swagger

@Singleton
class GoogleSettingController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s
  protected val accountType = "google"

  @Inject private var settingsService: SettingsService = _

  postWithDoc(s"/social_accounts/$accountType") { o =>
    o.summary(s"Connect to $accountType")
      .tag("Social Accounts")
      .request[PostSocialAccount]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith(Status.BadRequest.code, validationErrorMessage)

  } { request: PostSocialAccount =>
    settingsService.connectSocialAccount(
      accountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc(s"/social_accounts/$accountType") { o =>
    o.summary(s"Disconnect from $accountType")
      .tag("Social Accounts")
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { _: Request =>
    settingsService.disconnectSocialAccount(
      accountType,
      SessionContext.id
    ).map(_ => response.noContent)
  }


}
