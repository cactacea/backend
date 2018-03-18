package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import io.github.cactacea.backend.models.requests.setting._
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.{AdvertisementSetting, PushNotificationSetting}
import io.github.cactacea.core.util.auth.SessionContext
import io.swagger.models.Swagger

@Singleton
class SettingsController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Settings"

  @Inject private var settingsService: SettingsService = _
  @Inject private var deviceTokenService: DevicesService = _

  getWithDoc(c.rootPath + "/session/push_notification") { o =>
    o.summary("Get this push notification settings")
      .tag(tagName)
      .responseWith[PushNotificationSetting](Status.Ok.code, successfulMessage)

  } { _: Request =>
    settingsService.findPushNotificationSettings(
      SessionContext.id
    )
  }

  putWithDoc(c.rootPath + "/session/push_notification") { o =>
    o.summary("Update ths push notification settings")
      .tag(tagName)
      .responseWith(Status.NoContent.code, successfulMessage)


  } { request: PutNotificationSetting =>
    settingsService.updatePushNotificationSettings(
      request.groupInvitation,
      request.followerFeed,
      request.feedComment,
      request.groupMessage,
      request.directMessage,
      request.showMessage,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc(c.rootPath + "/session/advertisement") { o =>
    o.summary("Get the advertisement settings")
      .tag(tagName)
      .responseWith[AdvertisementSetting](Status.Ok.code, successfulMessage)

  } { _: Request =>
    settingsService.findAdvertisementSettings(
      SessionContext.id
    )
  }

  putWithDoc(c.rootPath + "/session/advertisement") { o =>
    o.summary("Update the advertisement settings")
      .tag(tagName)
      .request[PutAdvertisementSetting]
      .responseWith(Status.NoContent.code, successfulMessage)


  } { request: PutAdvertisementSetting =>
    settingsService.updateAdvertisementSettings(
      request.ad1,
      request.ad2,
      request.ad3,
      request.ad4,
      request.ad5,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc(c.rootPath + "/session/push_token") { o =>
    o.summary("Update device push token")
      .tag(tagName)
      .request[PostDevicePushToken]
      .responseWith(Status.NoContent.code, successfulMessage)


  } { request: PostDevicePushToken =>
    deviceTokenService.update(
      request.pushToken,
      SessionContext.id,
      SessionContext.udid
    ).map(_ => response.noContent)
  }

  postWithDoc(c.rootPath + "/session/status") { o =>
    o.summary("Update device status")
      .tag(tagName)
      .request[PostActiveStatus]
      .responseWith(Status.NoContent.code, successfulMessage)


  } { request: PostActiveStatus =>
    deviceTokenService.update(
      request.status,
      SessionContext.id,
      SessionContext.udid
    ).map(_ => response.noContent)
  }

}


