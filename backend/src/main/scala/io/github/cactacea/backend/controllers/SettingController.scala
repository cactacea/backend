package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.setting._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class SettingController @Inject()(settingsService: SettingsService, deviceTokenService: DevicesService) extends Controller {

  get("/social_accounts") { request: GetSocialAccounts =>
    settingsService.findSocialAccounts(
      request.session.id
    )
  }

  post("/social_accounts/:account_type") { request: PostSocialAccount =>
    settingsService.connectSocialAccount(
      request.socialAccountType,
      request.accessTokenKey,
      request.accessTokenSecret,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/social_accounts/:account_type") { request: DeleteSocialAccount =>
    settingsService.disconnectSocialAccount(
      request.socialAccountType,
      request.session.id
    ).map(_ => response.noContent)
  }

  get("/push_notification") { request: GetPushNotificationSetting =>
    settingsService.findPushNotificationSettings(
      request.session.id
    )
  }

  put("/push_notification") { request: PutNotificationSetting =>
    settingsService.updatePushNotificationSettings(
      request.notificationSetting,
      request.session.id
    ).map(_ => response.noContent)
  }

  get("/advertisement") { request: GetAdvertisementSetting =>
    settingsService.findAdvertisementSettings(
      request.session.id
    )
  }

  put("/advertisement") { request: PutAdvertisementSetting =>
    settingsService.updateAdvertisementSettings(
      request.advertisementSetting,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/devices") { request: PostDeviceToken =>
    deviceTokenService.update(
      request.session.id,
      request.session.udid,
      request.pushToken
    ).map(_ => response.noContent)
  }


}


