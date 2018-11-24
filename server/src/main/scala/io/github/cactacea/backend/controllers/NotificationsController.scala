package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.notification.GetNotifications
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services.NotificationsService
import io.github.cactacea.backend.core.domain.models.Notification
import io.swagger.models.Swagger

@Singleton
class NotificationsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, notificationsService: NotificationsService, s: Swagger)
  extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithPermission("/notifications")(Permissions.basic) { o =>
      o.summary("Search notifications")
        .tag(notificationsTag)
        .operationId("findNotifications")
        .request[GetNotifications]
        .responseWith[Array[Notification]](Status.Ok.code, successfulMessage)
    } { request: GetNotifications =>
      notificationsService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.locales,
        SessionContext.id
      )
    }

  }

}
