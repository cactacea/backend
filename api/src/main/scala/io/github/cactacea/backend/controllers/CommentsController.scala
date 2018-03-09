package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.comment._
import io.github.cactacea.backend.models.responses.CommentCreated
import io.github.cactacea.core.application.services.{CommentLikesService, CommentsService}
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class CommentsController extends Controller {

  @Inject private var commentsService: CommentsService = _

  get("/comments") { request: GetComments =>
    commentsService.findAll(
      request.feedId,
      request.since,
      request.count,
      SessionContext.id
    )
  }

  post("/comments") { request: PostComment =>
    commentsService.create(
      request.feedId,
      request.commentMessage,
      SessionContext.id
    ).map(CommentCreated(_)).map(response.created(_))
  }

  get("/comments/:id") { request: GetComment =>
    commentsService.find(
      request.commentId,
      SessionContext.id
    )
  }

  delete("/comments/:id") { request: DeleteComment =>
    commentsService.delete(
      request.commentId,
      SessionContext.id
    )
  }

  @Inject private var commentLikesService: CommentLikesService = _

  get("/comments/:id/likes") { request: GetCommentLikes =>
    commentLikesService.findAccounts(
      request.commentId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/comments/:id/likes") { request: PostCommentLike =>
    commentLikesService.create(
      request.commentId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/comments/:id/likes") { request: DeleteCommentLike =>
    commentLikesService.delete(
      request.commentId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
