package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.models.requests.session.GetSessionMutes
import io.github.cactacea.backend.swagger.BackendController

import io.github.cactacea.core.application.services.MutesService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.AccountNotFound
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class MutesController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Mute"

  @Inject private var mutesService: MutesService = _

  getWithDoc("/session/mutes") { o =>
    o.summary("Get mute accounts.")
      .tag(tagName)
      .request[GetSessionMutes]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionMutes =>
    mutesService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/accounts/:id/mutes") { o =>
    o.summary("Mute a account")
      .tag(tagName)
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: PostMute =>
    mutesService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/accounts/:id/mutes") { o =>
    o.summary("UnMute a account")
      .tag(tagName)
      .request[DeleteMute]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteMute =>
    mutesService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}