package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.GetFavorites
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.models.responses.FeedCreated
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.AuthUserContext._

@Singleton
class FeedsController extends Controller {

  @Inject var feedsService: FeedsService = _

  get("/feeds") { request: GetFeeds =>
    feedsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/feeds") { request: PostFeed =>
    feedsService.create(
      request.feedMessage,
      request.mediumIds,
      request.tags,
      request.privacyType,
      request.contentWarning,
      request.session.id
    ).map(FeedCreated(_)).map(response.created(_))
  }

  get("/feeds/:id") { request: GetFeed =>
    feedsService.find(
      request.feedId,
      request.session.id
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
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/feeds/:id") { request: DeleteFeed =>
    feedsService.delete(
      request.feedId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/feeds/:id/reports") { request: PostFeedReport =>
    feedsService.report(
      request.feedId,
      request.reportType,
      request.session.id
    ).map(_ => response.noContent)
  }

  get("/session/feeds") { request: GetSessionFeeds =>
    feedsService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }



  @Inject var feedFavoritesService: FeedFavoritesService = _

  get("/session/favorites") { request: GetSessionFavoriteFeeds =>
    feedFavoritesService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  get("/feeds/:id/favorites") { request: GetFeedFavorites =>
    feedFavoritesService.findAccounts(
      request.feedId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/feeds/:id/favorites") { request: PostFeedFavorite =>
    feedFavoritesService.delete(
      request.feedId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/feeds/:id/favorites") { request: DeleteFeedFavorite =>
    feedFavoritesService.delete(
      request.feedId,
      request.session.id
    ).map(_ => response.noContent)
  }

  get("/accounts/:id/favorites") { request: GetFavorites =>
    feedFavoritesService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

}

