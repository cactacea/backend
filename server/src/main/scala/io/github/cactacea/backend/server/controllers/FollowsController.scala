package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.models.requests.user.{DeleteFollow, GetFollows, PostFollow}
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

    scope(followerList).getWithDoc("/users/:id/follows") { o =>
      o.summary("Get users list an user follows")
        .tag(usersTag)
        .operationId("findFollow")
        .request[GetFollows]
        .responseWith[Array[User]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetFollows =>
      followsService.find(
        request.id,
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(relationships).postWithDoc("/users/:id/follow") { o =>
      o.summary("Follow an user")
        .tag(usersTag)
        .operationId("followUser")
        .request[PostFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserAlreadyFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: PostFollow =>
      followsService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(relationships).deleteWithDoc("/users/:id/follow") { o =>
      o.summary("UnFollow an user")
        .tag(usersTag)
        .operationId("unfollowUser")
        .request[DeleteFollow]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserNotFollowed))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: DeleteFollow =>
      followsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }



}
