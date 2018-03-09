package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.{GetSessionFollowers, GetSessionFollows}
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class FollowersController extends Controller {

  @Inject private var followsService: FollowsService = _

  get("/accounts/:id/follows") { request: GetFollows =>
    followsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/accounts/:id/follows") { request: PostFollow =>
    followsService.create(
      request.accountId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/follows") { request: DeleteFollow =>
    followsService.delete(
      request.accountId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  get("/session/follows") { request: GetSessionFollows =>
    followsService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  @Inject private var followersService: FollowersService = _

  get("/accounts/:id/followers") { request: GetFollowers =>
    followersService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  get("/session/followers") { request: GetSessionFollowers =>
    followersService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }


}
