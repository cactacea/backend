package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.tweet.{DeleteTweetLike, GetTweetLikes, PostTweetLike}
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class TweetLikesController @Inject()(
                                      @Flag("cactacea.api.prefix") apiPrefix: String,
                                      tweetLikesService: TweetLikesService,
                                      f: CactaceaAuthenticationFilterFactory,
                                      s: Swagger
                                   ) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(tweets).getWithDoc("/tweets/:id/likes") { o =>
      o.summary("Get users list who set a like to a tweet")
        .tag(tweetsLikeTag)
        .operationId("findUsersLikedTweet")
        .request[GetTweetLikes]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound))))
    } { request: GetTweetLikes =>
      tweetLikesService.findUsers(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(tweetLikes).postWithDoc("/tweets/:id/likes") { o =>
      o.summary("Set a like on a tweet")
        .tag(tweetsLikeTag)
        .operationId("likeTweet")
        .request[PostTweetLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(TweetAlreadyLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound))))
    } { request: PostTweetLike =>
      tweetLikesService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(tweetLikes).deleteWithDoc("/tweets/:id/likes") { o =>
      o.summary("Remove a like on a tweet")
        .tag(tweetsLikeTag)
        .operationId("unlikeTweet")
        .request[DeleteTweetLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(TweetNotLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(TweetNotFound))))
    } { request: DeleteTweetLike =>
      tweetLikesService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}

