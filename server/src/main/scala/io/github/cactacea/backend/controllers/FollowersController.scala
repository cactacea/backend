package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFollowed, _}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
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
      o.summary("Get accounts list a account followed")
        .tag(followsTag)
        .operationId("findFollows")
        .request[GetFollows]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
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
      o.summary("Follow a account")
        .tag(followsTag)
        .operationId("follow")
        .request[PostFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: PostFollow =>
      followsService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    deleteWithPermission("/accounts/:id/follows")(Permissions.relationships) { o =>
      o.summary("UnFollow a account")
        .tag(followsTag)
        .operationId("unfollow")
        .request[DeleteFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: DeleteFollow =>
      followsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }



}
