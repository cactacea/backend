package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.models.requests.session.GetSessionMutes
import io.github.cactacea.core.application.services.MutesService
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class MutesController extends Controller {

  @Inject private var mutesService: MutesService = _

  get("/session/mutes") { request: GetSessionMutes =>
    mutesService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/accounts/:id/mutes") { request: PostMute =>
    mutesService.create(
      request.accountId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/mutes") { request: DeleteMute =>
    mutesService.delete(
      request.accountId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
