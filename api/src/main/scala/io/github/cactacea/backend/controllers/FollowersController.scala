package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.{GetSessionFollowers, GetSessionFollowing}
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.AccountNotFound
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class FollowersController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  @Inject private var followingService: FollowingService = _

  protected val tagName = "Followers"

  getWithDoc("/accounts/:id/following") { o =>
    o.summary("Get account's following")
      .tag(tagName)
      .request[GetFollowing]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: GetFollowing =>
    followingService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/accounts/:id/following") { o =>
    o.summary("Follow a account")
      .tag(tagName)
      .request[PostFollowing]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: PostFollowing =>
    followingService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/accounts/:id/following") { o =>
    o.summary("Unfollow a account")
      .tag(tagName)
      .request[DeleteFollowing]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith(Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteFollowing =>
    followingService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc("/session/following") { o =>
    o.summary("Get session's following")
      .tag(tagName)
      .request[GetSessionFollowing]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionFollowing =>
    followingService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  @Inject private var followersService: FollowersService = _

  getWithDoc("/accounts/:id/followers") { o =>
    o.summary("Get account's followers")
      .tag(tagName)
      .request[GetFollowers]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetFollowers =>
    followersService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc("/session/followers") { o =>
    o.summary("Get session's followers")
      .tag(tagName)
      .request[GetSessionFollowers]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionFollowers =>
    followersService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }


}
