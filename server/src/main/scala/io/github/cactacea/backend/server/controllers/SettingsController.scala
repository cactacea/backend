package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.NotificationSetting
import io.github.cactacea.backend.server.models.requests.setting.{PutDevice, PutNotificationSetting}
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class SettingsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    settingsService: SettingsService,
                                    deviceTokenService: DevicesService,
                                    f: CactaceaAuthenticationFilterFactory,
                                    s: Swagger
                                  ) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/session/push/notification")  { o =>
      o.summary("Get push notification settings")
        .tag(settingsTag)
        .operationId("findNotification")
        .responseWith[NotificationSetting](Status.Ok.code, successfulMessage)
    } { _: Request =>
      settingsService.findNotificationSettings(
        CactaceaContext.sessionId
      )
    }

    scope(basic).putWithDoc("/session/push/notification")  { o =>
      o.summary("Update ths push notification settings")
        .tag(settingsTag)
        .operationId("updateNotification")
        .request[PutNotificationSetting]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutNotificationSetting =>
      settingsService.updateNotificationSettings(
        request.tweet,
        request.comment,
        request.friendRequest,
        request.message,
        request.channelMessage,
        request.invitation,
        request.showMessage,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).putWithDoc("/session/device")  { o =>
      o.summary("Update device status")
        .tag(settingsTag)
        .operationId("updateDeviceStatus")
        .request[PutDevice]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutDevice =>
      deviceTokenService.update(
        request.udid,
        request.pushToken,
        CactaceaContext.deviceType,
        request.userAgent,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}


