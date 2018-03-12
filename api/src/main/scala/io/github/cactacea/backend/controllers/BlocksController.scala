package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.swagger.models.Swagger
import io.github.cactacea.backend.models.requests.account.{DeleteBlock, PostBlock}
import io.github.cactacea.backend.models.requests.session.GetSessionBlocks
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.BlocksService
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.AccountNotFound
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}

@Singleton
class BlocksController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Blocks"

  @Inject private var blocksService: BlocksService = _

  getWithDoc("/session/blocks") { o =>
    o.summary("Get blocked account list")
      .tag(tagName)
      .request[GetSessionBlocks]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionBlocks =>
    blocksService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/accounts/:id/blocks") { o =>
    o.summary("Block a account")
      .tag(tagName)
      .request[PostBlock]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: PostBlock =>
    blocksService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/accounts/:id/blocks") { o =>
    o.summary("Unblock a account")
        .tag(tagName)
      .request[DeleteBlock]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteBlock =>
    blocksService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
