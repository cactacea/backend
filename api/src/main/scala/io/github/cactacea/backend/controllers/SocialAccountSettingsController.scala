package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import io.github.cactacea.backend.models.requests.setting.{DeleteSocialAccount, PostSocialAccount}
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.SocialAccount
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.BadRequest
import io.swagger.models.Swagger

@Singleton
class SocialAccountSettingsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  @Inject private var settingsService: SettingsService = _

  getWithDoc("/social_accounts") { o =>
    o.summary("Get status abount social accounts.")
        .responseWith[Array[SocialAccount]](Status.Ok.code, successfulMessage)

  } { _: Request =>
    settingsService.findSocialAccounts(
      SessionContext.id
    )
  }

  postWithDoc("/social_accounts/:account_type") { o =>
    o.summary("Connect to a social account.")
      .request[PostSocialAccount]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith(Status.BadRequest.code, validationErrorMessage)

  } { request: PostSocialAccount =>
    settingsService.connectSocialAccount(
      request.socialAccountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/social_accounts/:account_type") { o =>
    o.summary("Disconnect from a social connect.")
      .request[DeleteSocialAccount]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: DeleteSocialAccount =>
    settingsService.disconnectSocialAccount(
      request.socialAccountType,
      SessionContext.id
    ).map(_ => response.noContent)
  }


}
