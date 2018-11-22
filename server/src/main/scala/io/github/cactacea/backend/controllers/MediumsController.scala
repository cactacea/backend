package io.github.cactacea.backend.controllers

import java.io.File

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.inject.annotations.Flag
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.services.MediumsService
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{MediumNotFound, NotAcceptableMimeTypeFound}
import io.github.cactacea.backend.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.media.MediaExtractor
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class MediumsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var mediumsService: MediumsService = _

  prefix(apiPrefix) {

    get("/mediums/:*") { request: Request =>
      response.ok.file(request.params("*"))
    }

    postWithPermission("/mediums")(Permissions.media) { o =>

      o.summary("Upload a medium")
        .tag(mediumsTag)
        .operationId("uploadMedium")
        .consumes("multipart/form-data")
        .formParam[File](name = "file", description = "Upload a medium file", true)
        .responseWith[MediumCreated](Status.Created.code, successfulMessage)
        .responseWith(Status.BadRequest.code, NotAcceptableMimeTypeFound.message)

    } { request: Request =>
      val multiParams = RequestUtils.multiParams(request)
      multiParams.toList.flatMap({ case (_, item) => MediaExtractor.extract(item.contentType, item.data) }).headOption match {
        case Some(media) =>
          mediumsService.create(
            media.width,
            media.height,
            media.data,
            media.contentType,
            SessionContext.id
          ).map({ case (id, url) => response.created(MediumCreated(id, url)) })
        case None =>
          Future.exception(CactaceaException(NotAcceptableMimeTypeFound))
      }
    }

    deleteWithPermission("/mediums/:id")(Permissions.media) { o =>
      o.summary("Delete a medium")
        .tag(mediumsTag)
        .operationId("deleteMedium")
        .request[DeleteMedium]
        .responseWith(Status.Ok.code, MediumNotFound.message)

    } { request: DeleteMedium =>
      mediumsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}