package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.{GetSessionFriendRequests, GetSessionFriends}
import io.github.cactacea.backend.models.responses.FriendRequestCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.core.domain.models.{Account, FriendRequest}
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.github.cactacea.core.util.responses.CactaceaError.{AccountAlreadyRequested, AccountNotFound, FriendRequestNotFound}
import io.swagger.models.Swagger

@Singleton
class FriendsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Friends"

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  getWithDoc("/session/requests") { o =>
    o.summary("Get session's friend requests")
      .tag(tagName)
      .request[GetSessionFriendRequests]
      .responseWith[Array[FriendRequest]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionFriendRequests =>
    friendRequestsService.findAll(
      request.since,
      request.offset,
      request.count,
      request.received,
      SessionContext.id
    )
  }

  getWithDoc("/session/friends") { o =>
    o.summary("Get session's friends")
      .tag(tagName)
      .request[GetSessionFriends]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  }  { request: GetSessionFriends =>
    friendsService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/session/requests/:id/accept") { o =>
    o.summary("Accept a friend request")
      .tag(tagName)
      .request[PostAcceptFriendRequest]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, FriendRequestNotFound.message)

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
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, FriendRequestNotFound.message)

  } { request: PostRejectFriendRequest =>
    friendRequestsService.reject(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc("/accounts/:id/friends") { o =>
    o.summary("Get account's friends")
      .tag(tagName)
      .request[GetFriends]
      .responseWith[Account](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: GetFriends =>
    friendsService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  deleteWithDoc("/accounts/:id/friends") { o =>
    o.summary("Get account's friends")
      .tag(tagName)
      .request[DeleteFriend]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteFriend =>
    friendsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/accounts/:id/requests") { o =>
    o.summary("Request a friend")
      .tag(tagName)
      .request[PostFriendRequest]
      .responseWith[FriendRequestCreated](Status.Created.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[BadRequest](Status.BadRequest.code, AccountAlreadyRequested.message)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: PostFriendRequest =>
    friendRequestsService.create(
      request.id,
      SessionContext.id
    ).map(FriendRequestCreated(_)).map(response.created(_))
  }

  deleteWithDoc("/accounts/:id/requests") { o =>
    o.summary("Delete a friend request")
      .tag(tagName)
      .request[DeleteFriendRequest]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[BadRequest](Status.BadRequest.code, FriendRequestNotFound.message)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteFriendRequest =>
    friendRequestsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
