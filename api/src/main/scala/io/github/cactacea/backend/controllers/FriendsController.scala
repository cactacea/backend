package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.GetSessionFriendRequests
import io.github.cactacea.backend.models.responses.FriendRequestCreated
import io.github.cactacea.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class FriendsController extends Controller {

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  get("/session/requests") { request: GetSessionFriendRequests =>
    friendRequestsService.findAll(
      request.since,
      request.offset,
      request.count,
      request.received,
      request.session.id
    )
  }

  post("/session/requests/:id/accept") { request: PostAcceptFriendRequest =>
    friendRequestsService.accept(
      request.friendRequestId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/session/requests/:id/reject") { request: PostRejectFriendRequest =>
    friendRequestsService.reject(
      request.friendRequestId,
      request.session.id
    ).map(_ => response.noContent)
  }

  get("/accounts/:id/friends") { request: GetFriends =>
    friendsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  delete("/accounts/:id/friends") { request: DeleteFriend =>
    friendsService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/accounts/:id/requests") { request: PostFriendRequest =>
    friendRequestsService.create(
      request.accountId,
      request.session.id
    ).map(FriendRequestCreated(_)).map(response.created(_))
  }

  delete("/accounts/:id/requests") { request: DeleteFriendRequest =>
    friendRequestsService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

}
