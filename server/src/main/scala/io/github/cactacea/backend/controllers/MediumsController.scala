package io.github.cactacea.backend.controllers

import java.io.File

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.MediumsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{MediumNotFound, NotAcceptableMimeTypeFound}
import io.github.cactacea.backend.models.requests.medium.DeleteMedium
import io.github.cactacea.backend.models.responses.MediumCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class MediumsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, mediumsService: MediumsService, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithPermission("/mediums/:*")(Permissions.media) { o =>

      o.summary("Get a medium")
        .tag(mediumsTag)

    }  { request: Request =>
      mediumsService.find(request)
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
      mediumsService.create(
        request,
        SessionContext.id
      ).map({ case (id, uri) => MediumCreated(id, uri) })
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
