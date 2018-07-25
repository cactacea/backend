package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.models.requests.session.GetSessionMutes
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services.MutesService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class MutesController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Mutes"

  @Inject private var mutesService: MutesService = _

  prefix(apiPrefix) {

    getWithPermission("/session/mutes")(Permissions.basic) { o =>
      o.summary("Get accounts list session account muted")
        .tag(tagName)
        .request[GetSessionMutes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)

    } { request: GetSessionMutes =>
      mutesService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/accounts/:id/mutes")(Permissions.relationships) { o =>
      o.summary("Mute this account")
        .tag(tagName)
        .request[PostMute]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[AccountAlreadyBlockedType]](AccountAlreadyBlocked.status.code, AccountAlreadyBlocked.message)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: PostMute =>
      mutesService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/accounts/:id/mutes")(Permissions.relationships) { o =>
      o.summary("UnMute this account")
        .tag(tagName)
        .request[DeleteMute]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[AccountNotBlockedType]](AccountNotBlocked.status.code, AccountNotBlocked.message)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: DeleteMute =>
      mutesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
