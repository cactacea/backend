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
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class FeedsController @Inject()(
                                 @Flag("cactacea.api.prefix") apiPrefix: String,
                                 feedsService: FeedsService,
                                 s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/feeds") { o =>
      o.summary("Find feeds")
        .tag(feedsTag)
        .operationId("findFeeds")
        .request[GetFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFeeds =>
      feedsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.feedPrivacyType,
        CactaceaContext.sessionId
      )
    }

    postWithDoc("/feeds") { o =>
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

    getWithDoc("/feeds/:id") { o =>
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

    putWithDoc("/feeds/:id") { o =>
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

    deleteWithDoc("/feeds/:id") { o =>
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

    postWithDoc("/feeds/:id/reports") { o =>
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

