package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.setting.PostSocialAccount
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.responses.CactaceaErrors.{SocialAccountAlreadyConnected, SocialAccountAlreadyConnectedType, SocialAccountNotConnected, SocialAccountNotConnectedType}
import io.swagger.models.Swagger

@Singleton
class GoogleSettingController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val accountType = "google"

  @Inject private var settingsService: SettingsService = _

  prefix(apiPrefix) {

    postWithPermission(s"/social_accounts/$accountType")(Permissions.basic) { o =>
      o.summary(s"Connect to $accountType")
        .tag("Social Accounts")
        .request[PostSocialAccount]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[SocialAccountAlreadyConnectedType]](SocialAccountAlreadyConnected.status.code, SocialAccountAlreadyConnected.message)

    } { request: PostSocialAccount =>
      settingsService.connectSocialAccount(
        accountType,
        request.accessTokenKey,
        request.accessTokenSecret,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission(s"/social_accounts/$accountType")(Permissions.basic) { o =>
      o.summary(s"Disconnect from $accountType")
        .tag("Social Accounts")
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[SocialAccountNotConnectedType]](SocialAccountNotConnected.status.code, SocialAccountNotConnected.message)

    } { _: Request =>
      settingsService.disconnectSocialAccount(
        accountType,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }



}
