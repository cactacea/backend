package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.{DeleteMute, PostMute}
import io.github.cactacea.backend.models.requests.session.GetSessionMutes
import io.github.cactacea.core.application.services.MutesService
import io.github.cactacea.core.util.auth.AuthUserContext._

@Singleton
class MutesController extends Controller {

  @Inject var mutesService: MutesService = _

  get("/session/mutes") { request: GetSessionMutes =>
    mutesService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/accounts/:id/mutes") { request: PostMute =>
    mutesService.create(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/mutes") { request: DeleteMute =>
    mutesService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

}
