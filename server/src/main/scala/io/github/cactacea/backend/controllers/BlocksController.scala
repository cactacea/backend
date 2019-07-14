package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.BlocksService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.account.{DeleteBlock, PostBlock}
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.auth.CactaceaContext

import io.swagger.models.Swagger

@Singleton
class BlocksController @Inject()(
                                  @Flag("cactacea.api.prefix") apiPrefix: String,
                                  blocksService: BlocksService,
                                  s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/accounts/:id/blocks") { o =>
      o.summary("Block a account")
        .tag(accountsTag)
        .operationId("block")
        .request[PostBlock]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(CanNotSpecifyMyself, AccountAlreadyBlocked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: PostBlock =>
      blocksService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    deleteWithDoc("/accounts/:id/blocks") { o =>
      o.summary("Unblock a account")
        .tag(accountsTag)
        .operationId("unblock")
        .request[DeleteBlock]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(CanNotSpecifyMyself, AccountNotBlocked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: DeleteBlock =>
      blocksService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
