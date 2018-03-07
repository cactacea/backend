package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.request.RequestUtils
import io.github.cactacea.backend.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.core.application.services.MediumsService
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class MediumsController extends Controller {

  @Inject private var mediumsService: MediumsService = _

  get("/mediums/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

  post("/mediums") { request: Request =>
    mediumsService.create(
      RequestUtils.multiParams(request),
      request.id
    ).map(_.map({case (id, url) => MediumCreated(id, url)}))
  }

  delete("/mediums/:id") { request: DeleteMedium =>
    mediumsService.delete(
      request.id,
      request.session.id
    ).map(_ => response.noContent)
  }
}