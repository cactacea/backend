package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.MutesService
import io.github.cactacea.backend.core.util.responses.CactaceaError
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class MutesController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger = s

  @Inject private var mutesService: MutesService = _

  prefix(apiPrefix) {

    postWithPermission("/accounts/:id/mutes")(Permissions.relationships) { o =>
      o.summary("Mute this account")
        .tag(mutesTag)
        .operationId("muteAccount")
        .request[PostMute]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(AccountNotFound))
        .responseWithArray[CactaceaError](Status.BadRequest, Array(AccountAlreadyBlocked))
    } { request: PostMute =>
      mutesService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/accounts/:id/mutes")(Permissions.relationships) { o =>
      o.summary("UnMute this account")
        .tag(mutesTag)
        .operationId("unmuteAccount")
        .request[DeleteMute]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(AccountNotFound))
        .responseWithArray[CactaceaError](Status.BadRequest, Array(AccountNotBlocked))
    } { request: DeleteMute =>
      mutesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
