package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.timeline.GetTimeline
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.TimelineService
import io.github.cactacea.core.domain.models.TimelineFeed
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError._
import io.swagger.models.Swagger

@Singleton
class TimelineController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  @Inject private var timelineService: TimelineService = _

  getWithDoc("/timeline") { o =>
    o.summary("Get timeline")
      .tag("Timeline")
      .request[GetTimeline]
      .responseWith[Array[TimelineFeed]](Status.Ok.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)

  } { request: GetTimeline =>
    timelineService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}
