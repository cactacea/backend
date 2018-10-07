package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.util.responses.BadRequest
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{SocialAccountAlreadyConnected, SocialAccountNotConnected}
import io.github.cactacea.backend.models.requests.setting.{DeleteSocialAccount, PostSocialAccount}
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class SocialAccountSettingsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var settingsService: SettingsService = _

  prefix(apiPrefix) {

    postWithPermission(s"/social_accounts")(Permissions.basic) { o =>
      o.summary(s"Connect to social account")
        .tag(socialAccountsTag)
        .operationId("connect")
        .request[PostSocialAccount]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(SocialAccountAlreadyConnected))
    } { request: PostSocialAccount =>
      settingsService.connectSocialAccount(
        request.providerId,
        request.accessTokenKey,
        request.accessTokenSecret,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission(s"/social_accounts")(Permissions.basic) { o =>
      o.summary(s"Disconnect from social account")
        .tag(socialAccountsTag)
        .operationId("disconnect")
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(SocialAccountNotConnected))
    } { request: DeleteSocialAccount =>
      settingsService.disconnectSocialAccount(
        request.providerId,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
