package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.MutesService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class MutesController @Inject()(
                                 @Flag("cactacea.api.prefix") apiPrefix: String,
                                 s: Swagger,
                                 mutesService: MutesService,
                               ) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithPermission("/accounts/:id/mutes")(Permissions.relationships) { o =>
      o.summary("Mute a account")
        .tag(accountsTag)
        .operationId("mute")
        .request[PostMute]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyBlocked))))
    } { request: PostMute =>
      mutesService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    deleteWithPermission("/accounts/:id/mutes")(Permissions.relationships) { o =>
      o.summary("Unmute a account")
        .tag(accountsTag)
        .operationId("unmute")
        .request[DeleteMute]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotBlocked))))
    } { request: DeleteMute =>
      mutesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
