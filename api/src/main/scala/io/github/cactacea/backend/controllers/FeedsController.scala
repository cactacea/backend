package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.GetFavorites
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

  post("/feeds/:id/reports") { request: PostFeedReport =>
    feedsService.report(
      request.feedId,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var feedFavoritesService: FeedFavoritesService = _

  get("/feeds/:id/favorites") { request: GetFeedFavorites =>
    feedFavoritesService.findAccounts(
      request.feedId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/feeds/:id/favorites") { request: PostFeedFavorite =>
    feedFavoritesService.delete(
      request.feedId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/feeds/:id/favorites") { request: DeleteFeedFavorite =>
    feedFavoritesService.delete(
      request.feedId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  get("/accounts/:id/favorites") { request: GetFavorites =>
    feedFavoritesService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}

