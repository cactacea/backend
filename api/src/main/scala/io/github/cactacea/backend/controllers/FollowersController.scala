package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.session.{GetSessionFollowers, GetSessionFollowing}
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors.{AccountNotFollowed, _}
import io.swagger.models.Swagger

@Singleton
class FollowersController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  @Inject private var followingService: FollowingService = _

  protected val tagName = "Followers"

  getWithDoc(c.rootPath + "/accounts/:id/following") { o =>
    o.summary("Get accounts list this user followed")
      .tag(tagName)
      .request[GetFollowing]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: GetFollowing =>
    followingService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc(c.rootPath + "/accounts/:id/following") { o =>
    o.summary("Follow this account")
      .tag(tagName)
      .request[PostFollowing]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[AccountAlreadyFollowedType]](AccountAlreadyFollowed.status.code, AccountAlreadyFollowed.message)
      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: PostFollowing =>
    followingService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc(c.rootPath + "/accounts/:id/following") { o =>
    o.summary("UnFollow this account")
      .tag(tagName)
      .request[DeleteFollowing]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[AccountNotFollowedType]](AccountNotFollowed.status.code, AccountNotFollowed.message)
      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: DeleteFollowing =>
    followingService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc(c.rootPath + "/session/following") { o =>
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

  @Inject private var followersService: FollowersService = _

  getWithDoc(c.rootPath + "/accounts/:id/followers") { o =>
    o.summary("Get accounts list this account is followed by")
      .tag(tagName)
      .request[GetFollowers]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: GetFollowers =>
    followersService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc(c.rootPath + "/session/followers") { o =>
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
