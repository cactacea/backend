package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFollowed, _}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.swagger.SwaggerController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.{OAuthController, Permissions}
import io.swagger.models.Swagger

@Singleton
class FollowingsController @Inject()(
                                      @Flag("cactacea.api.prefix") apiPrefix: String,
                                      followingsService: FollowingsService,
                                      s: Swagger) extends SwaggerController with OAuthController {

  protected implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithPermission("/accounts/:id/following")(Permissions.followerList) { o =>
      o.summary("Get accounts list a account following")
        .tag(accountsTag)
        .operationId("findFollowing")
        .request[GetFollowing]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFollowing =>
      followingsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    postWithPermission("/accounts/:id/follow")(Permissions.relationships) { o =>
      o.summary("Follow a account")
        .tag(accountsTag)
        .operationId("follow")
        .request[PostFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: PostFollow =>
      followingsService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    deleteWithPermission("/accounts/:id/follow")(Permissions.relationships) { o =>
      o.summary("UnFollow a account")
        .tag(accountsTag)
        .operationId("unfollow")
        .request[DeleteFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: DeleteFollow =>
      followingsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }



}
