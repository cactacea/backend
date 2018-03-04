package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.{DeleteBlock, PostBlock}
import io.github.cactacea.backend.models.requests.session.GetSessionBlocks
import io.github.cactacea.core.application.services.BlocksService
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class BlocksController extends Controller {

  @Inject var blocksService: BlocksService = _

  get("/session/blocks") { request: GetSessionBlocks =>
    blocksService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/accounts/:id/blocks") { request: PostBlock =>
    blocksService.create(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/blocks") { request: DeleteBlock =>
    blocksService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

}
