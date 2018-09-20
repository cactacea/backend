package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.{GetSessionFollowers, GetSessionFollowing}
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFollowed, _}
import io.swagger.models.Swagger

@Singleton
class FollowersController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  @Inject private var followingService: FollowingService = _
  @Inject private var followersService: FollowersService = _

  protected val tagName = "Followers"

  prefix(apiPrefix) {

    getWithPermission("/accounts/:id/following")(Permissions.followerList) { o =>
      o.summary("Get accounts list this user followed")
        .tag(tagName)
        .request[GetFollowing]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetFollowing =>
      followingService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/accounts/:id/following")(Permissions.relationships) { o =>
      o.summary("Follow this account")
        .tag(tagName)
        .request[PostFollowing]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[AccountAlreadyFollowed.type]](AccountAlreadyFollowed.status.code, AccountAlreadyFollowed.message)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: PostFollowing =>
      followingService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/accounts/:id/following")(Permissions.relationships) { o =>
      o.summary("UnFollow this account")
        .tag(tagName)
        .request[DeleteFollowing]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[AccountNotFollowed.type]](AccountNotFollowed.status.code, AccountNotFollowed.message)
        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: DeleteFollowing =>
      followingService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    getWithPermission("/session/following")(Permissions.followerList) { o =>
      o.summary("Get accounts list session account followed")
        .tag(tagName)
        .request[GetSessionFollowing]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)


    } { request: GetSessionFollowing =>
      followingService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/followers")(Permissions.followerList) { o =>
      o.summary("Get accounts list this account is followed by")
        .tag(tagName)
        .request[GetFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)

        .responseWith[Array[AccountNotFound.type]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetFollowers =>
      followersService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/followers")(Permissions.followerList) { o =>
      o.summary("Get accounts list session account is followed by")
        .tag(tagName)
        .request[GetSessionFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)


    } { request: GetSessionFollowers =>
      followersService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

  }



}
