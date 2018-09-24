package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.setting.{PostActiveStatus, PostDevicePushToken, PutAdvertisementSetting, PutNotificationSetting}
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.{PushNotificationSetting}
import io.swagger.models.Swagger

@Singleton
class SettingsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  protected val tagName = "Settings"

  @Inject private var settingsService: SettingsService = _
  @Inject private var deviceTokenService: DevicesService = _

  prefix(apiPrefix) {

    getWithPermission("/session/push_notification") (Permissions.basic) { o =>
      o.summary("Get this push notification settings")
        .tag(tagName)
        .responseWith[PushNotificationSetting](Status.Ok.code, successfulMessage)

    } { _: Request =>
      settingsService.findPushNotificationSettings(
        SessionContext.id
      )
    }

    putWithPermission("/session/push_notification") (Permissions.basic) { o =>
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

    postWithPermission("/session/push_token") (Permissions.basic) { o =>
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

    postWithPermission("/session/status") (Permissions.basic) { o =>
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

}


