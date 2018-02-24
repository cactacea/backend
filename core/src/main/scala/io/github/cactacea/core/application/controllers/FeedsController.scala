package io.github.cactacea.core.application.controllers

import com.google.inject.Inject
import com.twitter.finatra.http.Controller
import io.github.cactacea.core.application.requests.comment._
import io.github.cactacea.core.application.requests.feed._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.AuthUserContext._

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
    ).map(response.created(_))
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



  @Inject var commentsService: CommentsService = _

  get("/comments") { request: GetComments =>
    commentsService.findAll(
      request.feedId,
      request.since,
      request.count,
      request.session.id
    )
  }

  post("/comments") { request: PostComment =>
    commentsService.create(
      request.feedId,
      request.commentMessage,
      request.session.id
    )
  }

  get("/comments/:id") { request: GetComment =>
    commentsService.find(
      request.commentId,
      request.session.id
    )
  }

  delete("/comments/:id") { request: DeleteComment =>
    commentsService.delete(
      request.commentId,
      request.session.id
    )
  }

  post("/comments/:id/reports") { request: PostCommentReport =>
    commentsService.report(
      request.commentId,
      request.reportType,
      request.session.id
    )
  }



  @Inject var commentFavoritesService: CommentFavoritesService = _

  get("/comments/:id/favorites") { request: GetCommentFavorites =>
    commentFavoritesService.findAccounts(
      request.commentId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/comments/:id/favorites") { request: PostCommentFavorite =>
    commentFavoritesService.create(
      request.commentId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/comments/:id/favorites") { request: DeleteCommentFavorite =>
    commentFavoritesService.delete(
      request.commentId,
      request.session.id
    ).map(_ => response.noContent)
  }



}

