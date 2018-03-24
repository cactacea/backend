package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.{DeleteFriendRequest, PostAcceptFriendRequest, PostFriendRequest, PostRejectFriendRequest}
import io.github.cactacea.backend.models.requests.session.GetSessionFriendRequests
import io.github.cactacea.backend.models.responses.FriendRequestCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.backend.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class RequestsController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Friend Requests"

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  prefix(apiPrefix) {

    getWithPermission("/session/requests")(Permissions.basic) { o =>
      o.summary("Get friend requests list session account created or received")
        .tag(tagName)
        .request[GetSessionFriendRequests]
        .responseWith[Array[FriendRequest]](Status.Ok.code, successfulMessage)


    } { request: GetSessionFriendRequests =>
      friendRequestsService.findAll(
        request.since,
        request.offset,
        request.count,
        request.received,
        SessionContext.id
      )
    }

    postWithPermission("/session/requests/:id/accept")(Permissions.friendRequests) { o =>
      o.summary("Accept a friend request")
        .tag(tagName)
        .request[PostAcceptFriendRequest]
        .responseWith(Status.NoContent.code, successfulMessage)

        .responseWith[Array[FriendRequestNotFoundType]](FriendRequestNotFound.status.code, FriendRequestNotFound.message)

    } { request: PostAcceptFriendRequest =>
      friendRequestsService.accept(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/session/requests/:id/reject")(Permissions.friendRequests) { o =>
      o.summary("Reject a friend request")
        .tag(tagName)
        .request[PostRejectFriendRequest]
        .responseWith(Status.NoContent.code, successfulMessage)

        .responseWith[Array[FriendRequestNotFoundType]](FriendRequestNotFound.status.code, FriendRequestNotFound.message)

    } { request: PostRejectFriendRequest =>
      friendRequestsService.reject(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/accounts/:id/requests")(Permissions.friendRequests) { o =>
      o.summary("Create a friend request to this account")
        .tag(tagName)
        .request[PostFriendRequest]
        .responseWith[FriendRequestCreated](Status.Created.code, successfulMessage)

        .responseWith[AccountAlreadyRequestedType](AccountAlreadyRequested.status.code, AccountAlreadyRequested.message)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: PostFriendRequest =>
      friendRequestsService.create(
        request.id,
        SessionContext.id
      ).map(FriendRequestCreated(_)).map(response.created(_))
    }

    deleteWithPermission("/accounts/:id/requests")(Permissions.friendRequests) { o =>
      o.summary("Remove a friend request to this account")
        .tag(tagName)
        .request[DeleteFriendRequest]
        .responseWith(Status.NoContent.code, successfulMessage)

        .responseWith[FriendRequestNotFoundType](FriendRequestNotFound.status.code, FriendRequestNotFound.message)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: DeleteFriendRequest =>
      friendRequestsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
