package io.github.cactacea.backend.controllers

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.request.RequestUtils
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.core.application.services.MediumsService
import io.github.cactacea.core.util.auth.AuthUserContext._

class MediumsController extends Controller {

  @Inject var mediumsService: MediumsService = _

  post("/mediums") { request: Request =>
    mediumsService.create(
      RequestUtils.multiParams(request),
      request.id
    ).map(_.map({case (id, url) => MediumCreated(id, url)}))
  }

}