package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.models.requests.setting.{PostActiveStatus, PostDevicePushToken, PutNotificationSetting}
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class SettingsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    settingsService: SettingsService,
                                    deviceTokenService: DevicesService
                                  ) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  protected val tagName = "Settings"

  prefix(apiPrefix) {

    getWithDoc("/session/push_notification")  { o =>
      o.summary("Get push notification settings")
        .tag(tagName)
        .operationId("findPushNotificationSettings")
        .responseWith[PushNotificationSetting](Status.Ok.code, successfulMessage)
    } { _: Request =>
      settingsService.findPushNotificationSettings(
        CactaceaContext.sessionId
      )
    }

    putWithDoc("/session/push_notification")  { o =>
      o.summary("Update ths push notification settings")
        .tag(tagName)
        .operationId("updatePushNotificationSettings")
        .request[PutNotificationSetting]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutNotificationSetting =>
      settingsService.updatePushNotificationSettings(
        request.feed,
        request.comment,
        request.friendRequest,
        request.message,
        request.groupMessage,
        request.groupInvitation,
        request.showMessage,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    postWithDoc("/session/push_token")  { o =>
      o.summary("Update device push token")
        .tag(tagName)
        .operationId("updatePushToken")
        .request[PostDevicePushToken]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PostDevicePushToken =>
      deviceTokenService.update(
        request.pushToken,
        CactaceaContext.sessionId,
        request.udid
      ).map(_ => response.ok)
    }

    postWithDoc("/session/status")  { o =>
      o.summary("Update device status")
        .tag(tagName)
        .operationId("updateDeviceStatus")
        .request[PostActiveStatus]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PostActiveStatus =>
      deviceTokenService.update(
        request.status,
        CactaceaContext.sessionId,
        request.udid
      ).map(_ => response.ok)
    }

  }

}


