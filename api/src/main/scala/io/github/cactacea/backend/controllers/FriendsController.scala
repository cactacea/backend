package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.GetSessionFriends
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, AccountNotFriend}
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class FriendsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Friends"

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  getWithDoc("/session/friends") { o =>
    o.summary("Get friends list")
      .tag(tagName)
      .request[GetSessionFriends]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

  }  { request: GetSessionFriends =>
    friendsService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc("/accounts/:id/friends") { o =>
    o.summary("Get this account's friends list")
      .tag(tagName)
      .request[GetFriends]
      .responseWith[Account](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[NotFound]](Status.NotFound.code, AccountNotFound.message)

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
    o.summary("Remove friendship to this account")
      .tag(tagName)
      .request[DeleteFriend]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, AccountNotFriend.message)
      .responseWith[Array[NotFound]](Status.NotFound.code, AccountNotFound.message)

  } { request: DeleteFriend =>
    friendsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
