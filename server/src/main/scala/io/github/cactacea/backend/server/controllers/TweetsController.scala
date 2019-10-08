package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Tweet
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.tweet._
import io.github.cactacea.backend.server.models.responses.TweetCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class TweetsController @Inject()(
                                 @Flag("cactacea.api.prefix") apiPrefix: String,
                                 tweetsService: TweetsService,
                                 f: CactaceaAuthenticationFilterFactory,
                                 s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(tweets).postWithDoc("/tweets") { o =>
      o.summary("Post a tweet")
        .tag(tweetsTag)
        .operationId("postTweet")
        .request[PostTweet]
        .responseWith[TweetCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
    } { request: PostTweet =>
      tweetsService.create(
        request.message,
        request.mediumIds.map(_.toList),
        request.tags.map(_.toList),
        request.privacyType,
        request.contentWarning,
        request.expiration,
        CactaceaContext.sessionId
      ).map(TweetCreated(_)).map(response.created(_))
    }

    scope(tweets).getWithDoc("/tweets/:id") { o =>
      o.summary("Get basic information about a tweet")
        .tag(tweetsTag)
        .operationId("findTweet")
        .request[GetTweet]
        .responseWith[Tweet](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound))))
    } { request: GetTweet =>
      tweetsService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(tweets).putWithDoc("/tweets/:id") { o =>
      o.summary("Update a tweet")
        .tag(tweetsTag)
        .operationId("updateTweet")
        .request[PutTweet]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound, MediumNotFound))))
    } { request: PutTweet =>
      tweetsService.edit(
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

    scope(tweets).deleteWithDoc("/tweets/:id") { o =>
      o.summary("Delete a tweet")
        .tag(tweetsTag)
        .operationId("deleteTweet")
        .request[DeleteTweet]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound))))
    } { request: DeleteTweet =>
      tweetsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(tweets && reports).postWithDoc("/tweets/:id/reports") { o =>
      o.summary("Report a tweet")
        .tag(tweetsTag)
        .operationId("reportTweet")
        .request[PostTweetReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound))))
    } { request: PostTweetReport =>
      tweetsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}

