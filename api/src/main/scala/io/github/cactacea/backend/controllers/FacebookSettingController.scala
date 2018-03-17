package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import io.github.cactacea.backend.models.requests.setting.PostSocialAccount
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class FacebookSettingController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s
  protected val accountType = "facebook"

  @Inject private var settingsService: SettingsService = _

  postWithDoc(s"/social_accounts/$accountType") { o =>
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

  deleteWithDoc(s"/social_accounts/$accountType") { o =>
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
