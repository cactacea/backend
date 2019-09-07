package io.github.cactacea.backend.server.controllers

import java.io.File

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.MediumsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.server.models.responses.MediumCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class MediumsController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   mediumsService: MediumsService,
                                   f: CactaceaAuthenticationFilterFactory,
                                   s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/mediums/:*") { o =>

      o.summary("Get a medium")
        .tag(mediumsTag)
        .operationId("findMedium")
      o.responseWith[File](Status.Ok.code, successfulMessage)

    }  { request: Request =>
      mediumsService.find(request)
    }

    scope(media).postWithDoc("/mediums") { o =>

      o.summary("Upload a medium")
        .tag(mediumsTag)
        .operationId("uploadMedium")
        .consumes("multipart/form-data")
        .formParam[File](name = "file", description = "Upload a medium file", true)
        .responseWith[Array[MediumCreated]](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason,
            Some(CactaceaErrors(Seq(NotAcceptableMimeTypeFound, UploadFileNotFound, FileSizeLimitExceededError))))

    } { request: Request =>
      mediumsService.create(
        request,
        CactaceaContext.sessionId
      ).map(_.map({ case (id, uri) => MediumCreated(id, uri) }))
    }

    scope(media).deleteWithDoc("/mediums/:id") { o =>
      o.summary("Delete a medium")
        .tag(mediumsTag)
        .operationId("deleteMedium")
        .request[DeleteMedium]
        .responseWith(Status.Ok.code, MediumNotFound.message)

    } { request: DeleteMedium =>
      mediumsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
