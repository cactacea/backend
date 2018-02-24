package io.github.cactacea.core.application.controllers

import com.google.inject.Inject
import com.twitter.finatra.http.Controller
import io.github.cactacea.core.application.requests.timeline.GetTimeline
import io.github.cactacea.core.application.services.TimelineService
import io.github.cactacea.core.util.auth.AuthUserContext._

class TimelineController extends Controller{

  @Inject var timelineService: TimelineService = _

  get("/timeline") { request: GetTimeline =>
    timelineService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

}
