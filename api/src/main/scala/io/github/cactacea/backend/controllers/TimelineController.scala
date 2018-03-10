package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.timeline.GetTimeline
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.TimelineService
import io.github.cactacea.core.domain.models.TimelineFeed
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.BadRequest
import io.swagger.models.Swagger

@Singleton
class TimelineController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  @Inject private var timelineService: TimelineService = _

  getWithDoc("/timeline") { o =>
    o.summary("Get timeline")
      .request[GetTimeline]
      .responseWith[Array[TimelineFeed]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetTimeline =>
    timelineService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}
