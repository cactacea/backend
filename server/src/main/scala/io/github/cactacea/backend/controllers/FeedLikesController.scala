package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaError
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class FeedLikesController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger = s

  @Inject private var feedsService: FeedsService = _
  @Inject private var feedLikesService: FeedLikesService = _

  prefix(apiPrefix) {

    getWithPermission("/feeds/:id/likes")(Permissions.basic) { o =>
      o.summary("Get accounts list who set a like to this feed")
        .tag(feedsTag)
        .operationId("findFeedLikes")
        .request[GetFeedLikes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound))
    } { request: GetFeedLikes =>
      feedLikesService.findAccounts(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/feeds/:id/likes")(Permissions.feedLikes) { o =>
      o.summary("Set a like on this feed")
        .tag(feedsTag)
        .operationId("likeFeed")
        .request[PostFeedLike]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.BadRequest, Array(FeedAlreadyLiked))
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound))
    } { request: PostFeedLike =>
      feedLikesService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/feeds/:id/likes")(Permissions.feeds) { o =>
      o.summary("Remove a like on this feed")
        .tag(feedsTag)
        .operationId("unlikeFeed")
        .request[DeleteFeedLike]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.BadRequest, Array(FeedNotLiked))
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound))
    } { request: DeleteFeedLike =>
      feedLikesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}

