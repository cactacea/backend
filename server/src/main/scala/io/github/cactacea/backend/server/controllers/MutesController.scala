package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.MutesService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.models.requests.user.{DeleteMute, PostMute}
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class MutesController @Inject()(
                                 @Flag("cactacea.api.prefix") apiPrefix: String,
                                 mutesService: MutesService,
                                 f: CactaceaAuthenticationFilterFactory,
                                 s: Swagger
                               ) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).postWithDoc("/users/:id/mutes") { o =>
      o.summary("Mute an user")
        .tag(usersTag)
        .operationId("muteUser")
        .request[PostMute]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserAlreadyBlocked))))
    } { request: PostMute =>
      mutesService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).deleteWithDoc("/users/:id/mutes") { o =>
      o.summary("Unmute an user")
        .tag(usersTag)
        .operationId("unmuteUser")
        .request[DeleteMute]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserNotBlocked))))
    } { request: DeleteMute =>
      mutesService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
