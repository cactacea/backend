package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.InformationsService
import io.github.cactacea.backend.core.domain.models.Information
import io.github.cactacea.backend.server.models.requests.notification.GetNotifications
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.github.cactacea.backend.utils.RequestImplicits._
import io.swagger.models.Swagger

@Singleton
class InformationsController @Inject()(
                                        @Flag("cactacea.api.prefix") apiPrefix: String,
                                        informationsService: InformationsService,
                                        f: CactaceaAuthenticationFilterFactory,
                                        s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/informations") { o =>
      o.summary("Search informations")
        .tag(notificationsTag)
        .operationId("findInformations")
        .request[GetNotifications]
        .responseWith[Seq[Information]](Status.Ok.code, successfulMessage)
    } { request: GetNotifications =>
      informationsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.request.locales(),
        CactaceaContext.sessionId
      )
    }

  }

}
