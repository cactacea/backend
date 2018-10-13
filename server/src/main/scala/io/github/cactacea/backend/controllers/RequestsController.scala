package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.{BadRequest, NotFound}
import io.github.cactacea.backend.models.requests.account.{DeleteFriendRequest, PostFriendRequest}
import io.github.cactacea.backend.models.responses.FriendRequestCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class RequestsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  protected implicit val swagger: Swagger = s

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  prefix(apiPrefix) {

    postWithPermission("/accounts/:id/requests")(Permissions.friendRequests) { o =>
      o.summary("Create a friend request to this account")
        .tag(RequestsTag)
        .operationId("createFriendRequest")
        .request[PostFriendRequest]
        .responseWith[FriendRequestCreated](Status.Created.code, successfulMessage)
        .responseWithArray[BadRequest](Status.BadRequest, Array(AccountAlreadyRequested))
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound))

    } { request: PostFriendRequest =>
      friendRequestsService.create(
        request.id,
        SessionContext.id
      ).map(FriendRequestCreated(_)).map(response.created(_))
    }

    deleteWithPermission("/accounts/:id/requests")(Permissions.friendRequests) { o =>
      o.summary("Remove a friend request to this account")
        .tag(RequestsTag)
        .operationId("deleteRequest")
        .request[DeleteFriendRequest]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(AccountNotFound, FriendRequestNotFound))

    } { request: DeleteFriendRequest =>
      friendRequestsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
