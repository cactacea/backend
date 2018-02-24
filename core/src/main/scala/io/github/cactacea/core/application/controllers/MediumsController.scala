package io.github.cactacea.core.application.controllers

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.request.RequestUtils
import io.github.cactacea.core.application.services.MediumsService
import io.github.cactacea.core.util.auth.AuthUserContext._

class MediumsController extends Controller {

  @Inject var mediumsService: MediumsService = _

  post("/mediums") { request: Request =>
    mediumsService.create(
      RequestUtils.multiParams(request),
      request.id
    )
  }

}