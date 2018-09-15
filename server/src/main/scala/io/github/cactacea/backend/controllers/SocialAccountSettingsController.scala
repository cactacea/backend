package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.setting.{DeleteSocialAccount, PostSocialAccount}
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{SocialAccountAlreadyConnected, SocialAccountAlreadyConnectedType, SocialAccountNotConnected, SocialAccountNotConnectedType}
import io.swagger.models.Swagger

@Singleton
class SocialAccountSettingsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Social Accounts"

  @Inject private var settingsService: SettingsService = _

  prefix(apiPrefix) {

    postWithPermission(s"/social_accounts/:social_account_type")(Permissions.basic) { o =>
      o.summary(s"Connect to social account")
        .tag(tagName)
        .request[PostSocialAccount]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[SocialAccountAlreadyConnectedType]](SocialAccountAlreadyConnected.status.code, SocialAccountAlreadyConnected.message)

    } { request: PostSocialAccount =>
      settingsService.connectSocialAccount(
        request.providerId,
        request.accessTokenKey,
        request.accessTokenSecret,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission(s"/social_accounts/:social_account_type")(Permissions.basic) { o =>
      o.summary(s"Disconnect from social account")
        .tag(tagName)
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[SocialAccountNotConnectedType]](SocialAccountNotConnected.status.code, SocialAccountNotConnected.message)

    } { request: DeleteSocialAccount =>
      settingsService.disconnectSocialAccount(
        request.providerId,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
