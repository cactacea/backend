package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.BlocksService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.{BadRequest, NotFound}
import io.github.cactacea.backend.models.requests.account.{DeleteBlock, PostBlock}
import io.github.cactacea.backend.models.requests.session.GetSessionBlocks
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class BlocksController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var blocksService: BlocksService = _

  prefix(apiPrefix) {

    getWithPermission("/session/blocks")(Permissions.basic) { o =>
      o.summary("Get blocking accounts list")
        .tag(blocksTag)
        .operationId("findBlockingAccounts")
        .request[GetSessionBlocks]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionBlocks =>
      blocksService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/accounts/:id/blocks")(Permissions.relationships) { o =>
      o.summary("Block a account")
        .tag(blocksTag)
        .operationId("block")
        .request[PostBlock]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(CanNotSpecifyMyself, AccountAlreadyBlocked))
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound))
    } { request: PostBlock =>
      blocksService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    deleteWithPermission("/accounts/:id/blocks")(Permissions.relationships) { o =>
      o.summary("Unblock a account")
        .tag(blocksTag)
        .operationId("unblock")
        .request[DeleteBlock]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(CanNotSpecifyMyself, AccountNotBlocked))
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound))
    } { request: DeleteBlock =>
      blocksService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
