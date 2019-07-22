package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.FriendRequestsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.account.{DeleteFriendRequest, PostAcceptFriendRequest, PostFriendRequest, PostRejectFriendRequest}
import io.github.cactacea.backend.server.models.responses.FriendRequestCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class FriendRequestsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    s: Swagger,
                                    friendRequestsService: FriendRequestsService,
                                  ) extends CactaceaController {

  protected implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(relationships).postWithDoc("/accounts/:id/requests") { o =>
      o.summary("Create a friend request to a account")
        .tag(accountsTag)
        .operationId("request")
        .request[PostFriendRequest]
        .responseWith[FriendRequestCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyRequested))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: PostFriendRequest =>
      friendRequestsService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(FriendRequestCreated(_)).map(response.created(_))
    }

    scope(relationships).deleteWithDoc("/accounts/:id/requests") { o =>
      o.summary("Remove a friend request to a account")
        .tag(accountsTag)
        .operationId("unrequest")
        .request[DeleteFriendRequest]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound, FriendRequestNotFound))))

    } { request: DeleteFriendRequest =>
      friendRequestsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

  scope(relationships).postWithDoc("/requests/:id/accept") { o =>
    o.summary("Accept a friend request")
      .tag(friendRequestsTag)
      .operationId("acceptRequest")
      .request[PostAcceptFriendRequest]
      .responseWith(Status.Ok.code, successfulMessage)
      .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FriendRequestNotFound))))
  } { request: PostAcceptFriendRequest =>
    friendRequestsService.accept(
      request.id,
      CactaceaContext.sessionId
    ).map(_ => response.ok)
  }

  scope(relationships).postWithDoc("/requests/:id/reject") { o =>
    o.summary("Reject a friend request")
      .tag(friendRequestsTag)
      .operationId("rejectRequest")
      .request[PostRejectFriendRequest]
      .responseWith(Status.Ok.code, successfulMessage)
      .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FriendRequestNotFound))))
  } { request: PostRejectFriendRequest =>
    friendRequestsService.reject(
      request.id,
      CactaceaContext.sessionId
    ).map(_ => response.ok)
  }

}
