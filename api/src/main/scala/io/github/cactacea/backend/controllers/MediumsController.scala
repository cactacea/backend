package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.core.application.services.MediumsService
import io.github.cactacea.core.util.responses.CactaceaErrors.MediumNotFound
import io.swagger.models.Swagger

@Singleton
class MediumsController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Mediums"

  @Inject private var mediumsService: MediumsService = _

  prefix(apiPrefix) {

    get("/mediums/:*") { request: Request =>
      response.ok.file(request.params("*"))
    }

    postWithPermission("/mediums")(Permissions.media) { o =>
      o.summary("Upload a medium")
        .tag(tagName)

    } { request: Request =>
      mediumsService.create(
        RequestUtils.multiParams(request),
        SessionContext.id
      ).map(_.map({case (id, url) => MediumCreated(id, url)}))
    }

    deleteWithPermission("/mediums/:id")(Permissions.media) { o =>
      o.summary("Remove this medium")
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

}