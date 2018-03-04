package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.comment._
import io.github.cactacea.backend.models.responses.CommentCreated
import io.github.cactacea.core.application.services.{CommentFavoritesService, CommentsService}
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class CommentsController extends Controller {

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
    ).map(CommentCreated(_)).map(response.created(_))
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
