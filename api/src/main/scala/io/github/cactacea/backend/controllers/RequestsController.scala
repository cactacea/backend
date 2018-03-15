package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.GetSessionFriendRequests
import io.github.cactacea.backend.models.responses.FriendRequestCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.core.domain.models.FriendRequest
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountAlreadyRequested, AccountNotFound, FriendRequestNotFound}
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class RequestsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Friend Requests"

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  getWithDoc("/session/requests") { o =>
    o.summary("Get friend requests list session account created or received")
      .tag(tagName)
      .request[GetSessionFriendRequests]
      .responseWith[Array[FriendRequest]](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionFriendRequests =>
    friendRequestsService.findAll(
      request.since,
      request.offset,
      request.count,
      request.received,
      SessionContext.id
    )
  }

  postWithDoc("/session/requests/:id/accept") { o =>
    o.summary("Accept a friend request")
      .tag(tagName)
      .request[PostAcceptFriendRequest]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[NotFound]](Status.NotFound.code, FriendRequestNotFound.message)

  } { request: PostAcceptFriendRequest =>
    friendRequestsService.accept(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/session/requests/:id/reject") { o =>
    o.summary("Reject a friend request")
      .tag(tagName)
      .request[PostRejectFriendRequest]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[NotFound]](Status.NotFound.code, FriendRequestNotFound.message)

  } { request: PostRejectFriendRequest =>
    friendRequestsService.reject(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/accounts/:id/requests") { o =>
    o.summary("Create a friend request to this account")
      .tag(tagName)
      .request[PostFriendRequest]
      .responseWith[FriendRequestCreated](Status.Created.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[BadRequest](Status.BadRequest.code, AccountAlreadyRequested.message)
      .responseWith[Array[NotFound]](Status.NotFound.code, AccountNotFound.message)

  } { request: PostFriendRequest =>
    friendRequestsService.create(
      request.id,
      SessionContext.id
    ).map(FriendRequestCreated(_)).map(response.created(_))
  }

  deleteWithDoc("/accounts/:id/requests") { o =>
    o.summary("Remove a friend request to this account")
      .tag(tagName)
      .request[DeleteFriendRequest]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[BadRequest](Status.BadRequest.code, FriendRequestNotFound.message)
      .responseWith[Array[NotFound]](Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteFriendRequest =>
    friendRequestsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
