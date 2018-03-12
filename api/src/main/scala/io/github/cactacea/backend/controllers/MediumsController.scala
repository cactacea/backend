package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.request.RequestUtils
import io.github.cactacea.backend.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.backend.swagger.BackendController

import io.github.cactacea.core.application.services.MediumsService
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.MediumNotFound
import io.swagger.models.Swagger

@Singleton
class MediumsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Mediums"

  @Inject private var mediumsService: MediumsService = _

  get("/mediums/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

  postWithDoc("/mediums") { o =>
    o.summary("Post a medium")
      .tag(tagName)

  } { request: Request =>
    mediumsService.create(
      RequestUtils.multiParams(request),
      SessionContext.id
    ).map(_.map({case (id, url) => MediumCreated(id, url)}))
  }

  deleteWithDoc("/mediums/:id") { o =>
    o.summary("Delele a medium")
      .tag(tagName)
      .request[DeleteMedium]
      .responseWith(Status.NoContent.code, MediumNotFound.message)

  } { request: DeleteMedium =>
    mediumsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }
}