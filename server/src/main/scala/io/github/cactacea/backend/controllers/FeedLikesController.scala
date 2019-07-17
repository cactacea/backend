package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class FeedLikesController @Inject()(
                                     @Flag("cactacea.api.prefix") apiPrefix: String,
                                     feedLikesService: FeedLikesService,
                                     s: Swagger
                                   ) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/feeds/:id/likes") { o =>
      o.summary("Get accounts list who set a like to a feed")
        .tag(feedsLikeTag)
        .operationId("findAccountsLikedFeed")
        .request[GetFeedLikes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: GetFeedLikes =>
      feedLikesService.findAccounts(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    postWithDoc("/feeds/:id/likes") { o =>
      o.summary("Set a like on a feed")
        .tag(feedsLikeTag)
        .operationId("likeFeed")
        .request[PostFeedLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(FeedAlreadyLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: PostFeedLike =>
      feedLikesService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    deleteWithDoc("/feeds/:id/likes") { o =>
      o.summary("Remove a like on a feed")
        .tag(feedsLikeTag)
        .operationId("unlikeFeed")
        .request[DeleteFeedLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(FeedNotLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: DeleteFeedLike =>
      feedLikesService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}

