package io.github.cactacea.backend.controllers

import java.io.File

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.MediumsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.auth.CactaceaContext

import io.swagger.models.Swagger

@Singleton
class MediumsController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   mediumsService: MediumsService,
                                   s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/mediums/:*") { o =>

      o.summary("Get a medium")
        .tag(mediumsTag)
        .operationId("findMedium")
      o.responseWith[File](Status.Ok.code, successfulMessage)

    }  { request: Request =>
      mediumsService.find(request)
    }

    postWithDoc("/mediums") { o =>

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
        CactaceaContext.id
      ).map(_.map({ case (id, uri) => MediumCreated(id, uri) }))
    }

    deleteWithDoc("/mediums/:id") { o =>
      o.summary("Delete a medium")
        .tag(mediumsTag)
        .operationId("deleteMedium")
        .request[DeleteMedium]
        .responseWith(Status.Ok.code, MediumNotFound.message)

    } { request: DeleteMedium =>
      mediumsService.delete(
        request.id,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

  }

}
