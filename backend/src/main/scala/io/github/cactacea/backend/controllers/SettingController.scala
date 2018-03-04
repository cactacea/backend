package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.setting._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.AuthUserContext._

@Singleton
class SettingController extends Controller {

  @Inject var socialAccountsService: SocialAccountsService = _

  get("/social_accounts") { request: GetSocialAccounts =>
    socialAccountsService.find(
      request.session.id
    )
  }

  post("/social_accounts/:account_type") { request: PostSocialAccount =>
    socialAccountsService.connect(
      request.socialAccountType,
      request.session.id,
      request.accessToken
    ).map(_ => response.noContent)
  }

  delete("/social_accounts/:account_type") { request: DeleteSocialAccount =>
    socialAccountsService.disconnect(
      request.socialAccountType,
      request.session.id
    ).map(_ => response.noContent)
  }

  @Inject var notificationSettingsService: PushNotificationSettingsService = _

  get("/push_notification") { request: GetPushNotificationSetting =>
    notificationSettingsService.find(
      request.session.id
    )
  }

  put("/push_notification") { request: PutNotificationSetting =>
    notificationSettingsService.edit(
      request.notificationSetting,
      request.session.id
    ).map(_ => response.noContent)
  }

  @Inject var advertisementSettingsService: AdvertisementSettingsService = _

  get("/advertisement") { request: GetAdvertisementSetting =>
    advertisementSettingsService.find(
      request.session.id
    )
  }

  put("/advertisement") { request: PutAdvertisementSetting =>
    advertisementSettingsService.update(
      request.advertisementSetting,
      request.session.id
    ).map(_ => response.noContent)
  }

  @Inject var deviceTokenService: DevicesService = _

  post("/devices") { request: PostDeviceToken =>
    deviceTokenService.update(
      request.session.id,
      request.session.udid,
      request.pushToken
    ).map(_ => response.noContent)
  }


}


