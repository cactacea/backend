package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.{DeleteBlock, PostBlock}
import io.github.cactacea.backend.models.requests.session.GetSessionBlocks
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services.BlocksService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class BlocksController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  protected val tagName = "Blocks"

  @Inject private var blocksService: BlocksService = _

  prefix(apiPrefix) {

    getWithPermission("/session/blocks")(Permissions.basic) { o =>
      o.summary("Get blocking accounts list")
        .tag(tagName)
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
        .tag(tagName)
        .request[PostBlock]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[CanNotSpecifyMyself.type]](CanNotSpecifyMyself.status.code, CanNotSpecifyMyself.message)
        .responseWith[Array[AccountAlreadyBlocked.type]](AccountAlreadyBlocked.status.code, AccountAlreadyBlocked.message)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: PostBlock =>
      blocksService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/accounts/:id/blocks")(Permissions.relationships) { o =>
      o.summary("Unblock a account")
        .tag(tagName)
        .request[DeleteBlock]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[CanNotSpecifyMyself.type]](CanNotSpecifyMyself.status.code, CanNotSpecifyMyself.message)
        .responseWith[Array[AccountNotBlocked.type]](AccountNotBlocked.status.code, AccountNotBlocked.message)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: DeleteBlock =>
      blocksService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
