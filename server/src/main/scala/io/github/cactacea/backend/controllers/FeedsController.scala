package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.models.responses.FeedCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class FeedsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var feedsService: FeedsService = _
  @Inject private var feedLikesService: FeedLikesService = _

  prefix(apiPrefix) {

    getWithPermission("/feeds")(Permissions.basic) { o =>
      o.summary("Search feeds")
        .tag(feedsTag)
        .operationId("findFeeds")
        .request[GetFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
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
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
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
      o.summary("Get basic information about a feed")
        .tag(feedsTag)
        .operationId("findFeed")
        .request[GetFeed]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: GetFeed =>
      feedsService.find(
        request.id,
        SessionContext.id
      )
    }

    putWithPermission("/feeds/:id")(Permissions.feeds) { o =>
      o.summary("Update a feed")
        .tag(feedsTag)
        .operationId("updateFeed")
        .request[PutFeed]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound, MediumNotFound))))
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
      ).map(_ => response.ok)
    }

    deleteWithPermission("/feeds/:id")(Permissions.feeds) { o =>
      o.summary("Delete a feed")
        .tag(feedsTag)
        .operationId("deleteFeed")
        .request[DeleteFeed]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: DeleteFeed =>
      feedsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    postWithPermission("/feeds/:id/reports")(Permissions.reports) { o =>
      o.summary("Report a feed")
        .tag(feedsTag)
        .operationId("reportFeed")
        .request[PostFeedReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: PostFeedReport =>
      feedsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}

