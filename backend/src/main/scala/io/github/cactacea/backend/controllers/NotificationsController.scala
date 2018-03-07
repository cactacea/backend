package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.notification.GetNotifications
import io.github.cactacea.core.application.services.NotificationsService
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class NotificationsController extends Controller {

  @Inject private var notificationsService: NotificationsService = _

  get("/comments") { request: GetNotifications =>
    notificationsService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

}
