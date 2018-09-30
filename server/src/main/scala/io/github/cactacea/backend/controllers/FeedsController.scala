package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.util.responses.CactaceaError
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.models.responses.FeedCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class FeedsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger = s

  @Inject private var feedsService: FeedsService = _
  @Inject private var feedLikesService: FeedLikesService = _

  prefix(apiPrefix) {

    getWithPermission("/feeds")(Permissions.basic) { o =>
      o.summary("Search feeds")
        .tag(feedsTag)
        .operationId("findFeeds")
        .request[GetFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(AccountNotFound))
    } { request: GetFeeds =>
      feedsService.find(
        request.since,
        request.offset,
        request.count,
        request.privacyType,
        SessionContext.id
      )
    }

    postWithPermission("/feeds")(Permissions.feeds) { o =>
      o.summary("Post a feed")
        .tag(feedsTag)
        .operationId("postFeed")
        .request[PostFeed]
        .responseWith[FeedCreated](Status.Created.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(MediumNotFound))
    } { request: PostFeed =>
      feedsService.create(
        request.message,
        request.mediumIds.map(_.toList),
        request.tags.map(_.toList),
        request.privacyType,
        request.contentWarning,
        request.expiration,
        SessionContext.id
      ).map(FeedCreated(_)).map(response.created(_))
    }

    getWithPermission("/feeds/:id")(Permissions.basic) { o =>
      o.summary("Get basic information about this feed")
        .tag(feedsTag)
        .operationId("findFeed")
        .request[GetFeed]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound))
    } { request: GetFeed =>
      feedsService.find(
        request.id,
        SessionContext.id
      )
    }

    putWithPermission("/feeds/:id")(Permissions.feeds) { o =>
      o.summary("Update this feed")
        .tag(feedsTag)
        .operationId("updateFeed")
        .request[PutFeed]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound, MediumNotFound))
    } { request: PutFeed =>
      feedsService.edit(
        request.id,
        request.message,
        request.mediumIds.map(_.toList),
        request.tags.map(_.toList),
        request.privacyType,
        request.contentWarning,
        request.expiration,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/feeds/:id")(Permissions.feeds) { o =>
      o.summary("Delete this feed")
        .tag(feedsTag)
        .operationId("deleteFeed")
        .request[DeleteFeed]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound))
    } { request: DeleteFeed =>
      feedsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/feeds/:id/reports")(Permissions.reports) { o =>
      o.summary("Report this feed")
        .tag(feedsTag)
        .operationId("reportFeed")
        .request[PostFeedReport]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(FeedNotFound))
    } { request: PostFeedReport =>
      feedsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}

