package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Feed
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.feed._
import io.github.cactacea.backend.server.models.responses.FeedCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class FeedsController @Inject()(
                                 @Flag("cactacea.api.prefix") apiPrefix: String,
                                 feedsService: FeedsService,
                                 s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(feeds).postWithDoc("/feeds") { o =>
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
        CactaceaContext.sessionId
      ).map(FeedCreated(_)).map(response.created(_))
    }

    scope(feeds).getWithDoc("/feeds/:id") { o =>
      o.summary("Get basic information about a feed")
        .tag(feedsTag)
        .operationId("findFeed")
        .request[GetFeed]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: GetFeed =>
      feedsService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(feeds).putWithDoc("/feeds/:id") { o =>
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
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(feeds).deleteWithDoc("/feeds/:id") { o =>
      o.summary("Delete a feed")
        .tag(feedsTag)
        .operationId("deleteFeed")
        .request[DeleteFeed]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: DeleteFeed =>
      feedsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(feeds && reports).postWithDoc("/feeds/:id/reports") { o =>
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
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}

