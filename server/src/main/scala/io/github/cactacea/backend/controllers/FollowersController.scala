package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFollowed, _}
import io.github.cactacea.backend.core.util.responses.{BadRequest, NotFound}
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class FollowersController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  protected implicit val swagger: Swagger = s

  @Inject private var followsService: FollowsService = _
  @Inject private var followersService: FollowersService = _

  prefix(apiPrefix) {

    getWithPermission("/accounts/:id/follows")(Permissions.followerList) { o =>
      o.summary("Get accounts list this user followed")
        .tag(followsTag)
        .operationId("findFollows")
        .request[GetFollows]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound))
    } { request: GetFollows =>
      followsService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/accounts/:id/follows")(Permissions.relationships) { o =>
      o.summary("Follow this account")
        .tag(followsTag)
        .operationId("follow")
        .request[PostFollow]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(AccountAlreadyFollowed))
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound))
    } { request: PostFollow =>
      followsService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/accounts/:id/follows")(Permissions.relationships) { o =>
      o.summary("UnFollow this account")
        .tag(followsTag)
        .operationId("unfollow")
        .request[DeleteFollow]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(AccountNotFollowed))
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound))
    } { request: DeleteFollow =>
      followsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }



}
