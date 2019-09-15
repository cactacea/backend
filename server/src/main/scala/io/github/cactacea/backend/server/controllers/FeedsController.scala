package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.FeedsService
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.server.models.requests.notification.GetNotifications
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.github.cactacea.backend.utils.RequestImplicits._
import io.swagger.models.Swagger

@Singleton
class FeedsController @Inject()(
                                         @Flag("cactacea.api.prefix") apiPrefix: String,
                                         feedsService: FeedsService,
                                         f: CactaceaAuthenticationFilterFactory,
                                         s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/feeds") { o =>
      o.summary("Search feeds")
        .tag(notificationsTag)
        .operationId("findFeeds")
        .request[GetNotifications]
        .responseWith[Seq[Feed]](Status.Ok.code, successfulMessage)
    } { request: GetNotifications =>
      feedsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.request.locales(),
        CactaceaContext.sessionId
      )
    }

  }

}
