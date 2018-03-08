package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.timeline.GetTimeline
import io.github.cactacea.core.application.services.TimelineService
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class TimelineController extends Controller{

  @Inject private var timelineService: TimelineService = _

  get("/timeline") { request: GetTimeline =>
    timelineService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}
