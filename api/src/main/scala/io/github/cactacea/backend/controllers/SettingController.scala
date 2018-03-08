package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.setting._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class SettingController extends Controller {

  @Inject private var settingsService: SettingsService = _
  @Inject private var deviceTokenService: DevicesService = _

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

  get("/push_notification") { request: Request =>
    settingsService.findPushNotificationSettings(
      SessionContext.id
    )
  }

  put("/push_notification") { request: PutNotificationSetting =>
    settingsService.updatePushNotificationSettings(
      request.notificationSetting,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  get("/advertisement") { request: Request =>
    settingsService.findAdvertisementSettings(
      SessionContext.id
    )
  }

  put("/advertisement") { request: PutAdvertisementSetting =>
    settingsService.updateAdvertisementSettings(
      request.advertisementSetting,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  post("/devices") { request: PostDeviceToken =>
    deviceTokenService.update(
      request.pushToken,
      SessionContext.id,
      SessionContext.udid
    ).map(_ => response.noContent)
  }


}


