package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.setting.{DeleteSocialAccount, PostSocialAccount}
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext
import io.swagger.models.Swagger

@Singleton
class SocialAccountSettingsController @Inject()(s: Swagger) extends Controller with SwaggerController {

  implicit protected val swagger = s

  @Inject private var settingsService: SettingsService = _

  get("/social_accounts") { request: Request =>
    settingsService.findSocialAccounts(
      SessionContext.id
    )
  }

  post("/social_accounts/:account_type") { request: PostSocialAccount =>
    settingsService.connectSocialAccount(
      request.socialAccountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/social_accounts/:account_type") { request: DeleteSocialAccount =>
    settingsService.disconnectSocialAccount(
      request.socialAccountType,
      SessionContext.id
    ).map(_ => response.noContent)
  }


}
