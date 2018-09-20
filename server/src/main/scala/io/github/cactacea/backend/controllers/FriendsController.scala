package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.{DeleteFriend, GetFriends}
import io.github.cactacea.backend.models.requests.session.GetSessionFriends
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services.{FriendRequestsService, FriendsService}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, AccountNotFriend}
import io.swagger.models.Swagger

@Singleton
class FriendsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  protected val tagName = "Friends"

  @Inject private var friendRequestsService: FriendRequestsService = _
  @Inject private var friendsService: FriendsService = _

  prefix(apiPrefix) {

    getWithPermission("/session/friends")(Permissions.followerList) { o =>
      o.summary("Get friends list")
        .tag(tagName)
        .request[GetSessionFriends]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)

    }  { request: GetSessionFriends =>
      friendsService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/friends")(Permissions.followerList) { o =>
      o.summary("Get this account's friends list")
        .tag(tagName)
        .request[GetFriends]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetFriends =>
      friendsService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    deleteWithPermission("/accounts/:id/friends")(Permissions.relationships) { o =>
      o.summary("Remove friendship to this account")
        .tag(tagName)
        .request[DeleteFriend]
        .responseWith(Status.NoContent.code, successfulMessage)

        .responseWith[Array[AccountNotFriend.type]](AccountNotFriend.status.code, AccountNotFriend.message)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: DeleteFriend =>
      friendsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
