package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.PushNotificationSetting
import io.github.cactacea.backend.models.requests.setting.{PostActiveStatus, PostDevicePushToken, PutNotificationSetting}
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class SettingsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  protected val tagName = "Settings"

  @Inject private var settingsService: SettingsService = _
  @Inject private var deviceTokenService: DevicesService = _

  prefix(apiPrefix) {

    getWithPermission("/session/push_notification") (Permissions.basic) { o =>
      o.summary("Get this push notification settings")
        .tag(tagName)
        .operationId("findSessionPushNotificationSettings")
        .responseWith[PushNotificationSetting](Status.Ok.code, successfulMessage)
    } { _: Request =>
      settingsService.findPushNotificationSettings(
        SessionContext.id
      )
    }

    putWithPermission("/session/push_notification") (Permissions.basic) { o =>
      o.summary("Update ths push notification settings")
        .tag(tagName)
        .operationId("updateSessionPushNotificationSettings")
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutNotificationSetting =>
      settingsService.updatePushNotificationSettings(
        request.groupInvitation,
        request.followerFeed,
        request.feedComment,
        request.groupMessage,
        request.directMessage,
        request.showMessage,
        SessionContext.id
      ).map(_ => response.ok)
    }

    postWithPermission("/session/push_token") (Permissions.basic) { o =>
      o.summary("Update device push token")
        .tag(tagName)
        .operationId("updateSessionPushToken")
        .request[PostDevicePushToken]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PostDevicePushToken =>
      deviceTokenService.update(
        request.pushToken,
        SessionContext.id,
        SessionContext.udid
      ).map(_ => response.ok)
    }

    postWithPermission("/session/status") (Permissions.basic) { o =>
      o.summary("Update device status")
        .tag(tagName)
        .operationId("updateSessionDeviceStatus")
        .request[PostActiveStatus]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PostActiveStatus =>
      deviceTokenService.update(
        request.status,
        SessionContext.id,
        SessionContext.udid
      ).map(_ => response.ok)
    }

  }

}


