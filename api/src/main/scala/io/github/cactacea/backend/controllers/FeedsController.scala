package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.{GetFeeds, GetLikes}
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.models.responses.FeedCreated
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class FeedsController extends Controller {

  @Inject private var feedsService: FeedsService = _

  get("/feeds") { request: GetFeeds =>
    feedsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/feeds") { request: PostFeed =>
    feedsService.create(
      request.feedMessage,
      request.mediumIds,
      request.tags,
      request.privacyType,
      request.contentWarning,
      request.expiration,
      SessionContext.id
    ).map(FeedCreated(_)).map(response.created(_))
  }

  get("/feeds/:id") { request: GetFeed =>
    feedsService.find(
      request.feedId,
      SessionContext.id
    )
  }

  put("/feeds/:id") { request: PutFeed =>
    feedsService.edit(
      request.feedId,
      request.feedMessage,
      request.mediumIds,
      request.tags,
      request.privacyType,
      request.contentWarning,
      request.expiration,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/feeds/:id") { request: DeleteFeed =>
    feedsService.delete(
      request.feedId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  get("/accounts/:id/feeds") { request: GetFeeds =>
    feedsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  @Inject private var feedLikesService: FeedLikesService = _

  get("/feeds/:id/likes") { request: GetFeedLikes =>
    feedLikesService.findAccounts(
      request.feedId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/feeds/:id/likes") { request: PostFeedLike =>
    feedLikesService.delete(
      request.feedId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/feeds/:id/likes") { request: DeleteFeedLike =>
    feedLikesService.delete(
      request.feedId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  get("/accounts/:id/likes") { request: GetLikes =>
    feedLikesService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  get("/session/feeds") { request: GetSessionFeeds =>
    feedsService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  get("/session/likes") { request: GetSessionLikedFeeds =>
    feedLikesService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}

