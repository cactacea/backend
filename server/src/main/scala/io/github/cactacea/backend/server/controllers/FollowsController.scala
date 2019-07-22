package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.models.requests.account.{DeleteFollow, GetFollow, PostFollow}
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class FollowsController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   followsService: FollowsService,
                                   s: Swagger) extends CactaceaController {

  protected implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(followerList).getWithDoc("/accounts/:id/follows") { o =>
      o.summary("Get accounts list a account follows")
        .tag(accountsTag)
        .operationId("findFollow")
        .request[GetFollow]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFollow =>
      followsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(relationships).postWithDoc("/accounts/:id/follow") { o =>
      o.summary("Follow a account")
        .tag(accountsTag)
        .operationId("followAccount")
        .request[PostFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: PostFollow =>
      followsService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(relationships).deleteWithDoc("/accounts/:id/follow") { o =>
      o.summary("UnFollow a account")
        .tag(accountsTag)
        .operationId("unfollowAccount")
        .request[DeleteFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNotFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: DeleteFollow =>
      followsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }



}
