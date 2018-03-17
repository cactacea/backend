package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.notification.GetNotifications
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.NotificationsService
import io.github.cactacea.core.domain.models.Notification
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError._
import io.swagger.models.Swagger

@Singleton
class NotificationsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Notifications"

  @Inject private var notificationsService: NotificationsService = _

  getWithDoc("/notifications") { o =>
    o.summary("Search notifications")
      .request[GetNotifications]
      .responseWith[Array[Notification]](Status.Ok.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)

  } { request: GetNotifications =>
    notificationsService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}
