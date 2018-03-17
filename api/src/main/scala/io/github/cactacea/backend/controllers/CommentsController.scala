package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.comment._
import io.github.cactacea.backend.models.responses.CommentCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.{CommentLikesService, CommentsService}
import io.github.cactacea.core.domain.models.{Account, Comment}
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors.{CommentAlreadyLiked, CommentNotFound, CommentNotLiked, _}
import io.swagger.models.Swagger

@Singleton
class CommentsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Comments"

  @Inject private var commentsService: CommentsService = _

  getWithDoc("/comments") { o =>
    o.summary("Search comments")
      .tag(tagName)
      .request[GetComments]
      .responseWith[Array[Comment]](Status.Ok.code, successfulMessage)

      .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

  } { request: GetComments =>
    commentsService.findAll(
      request.id,
      request.since,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/comments") { o =>
    o.summary("Create a comment on a feed")
      .tag(tagName)
      .request[PostComment]
      .responseWith[CommentCreated](Status.Created.code, successfulMessage)

      .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

  } { request: PostComment =>
    commentsService.create(
      request.id,
      request.commentMessage,
      SessionContext.id
    ).map(CommentCreated(_)).map(response.created(_))
  }

  getWithDoc("/comments/:id") { o =>
    o.summary("Get basic information about this comment")
      .tag(tagName)
      .request[GetComment]
      .responseWith[Comment](Status.Ok.code, successfulMessage)
      .responseWith[Array[CommentNotFoundType]](CommentNotFound.status.code, CommentNotFound.message)

  } { request: GetComment =>
    commentsService.find(
      request.id,
      SessionContext.id
    )
  }

  deleteWithDoc("/comments/:id") { o =>
    o.summary("Remove a comment")
      .tag(tagName)
      .request[DeleteComment]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[CommentNotFoundType]](CommentNotFound.status.code, CommentNotFound.message)

  } { request: DeleteComment =>
    commentsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var commentLikesService: CommentLikesService = _

  getWithDoc("/comments/:id/likes") { o =>
    o.summary("Get accounts list who liked on a comment")
      .tag(tagName)
      .request[GetCommentLikes]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[Array[CommentNotFoundType]](CommentNotFound.status.code, CommentNotFound.message)

  } { request: GetCommentLikes =>
    commentLikesService.findAccounts(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/comments/:id/likes") { o =>
    o.summary("Set a like on this comment")
      .tag(tagName)
      .request[PostCommentLike]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[CommentAlreadyLikedType]](CommentAlreadyLiked.status.code, CommentAlreadyLiked.message)
      .responseWith[Array[CommentNotFoundType]](CommentNotFound.status.code, CommentNotFound.message)

  } { request: PostCommentLike =>
    commentLikesService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/comments/:id/likes") { o =>
    o.summary("Remove a like on this comment")
      .tag(tagName)
      .request[DeleteCommentLike]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[CommentNotLikedType]](CommentNotLiked.status.code, CommentNotLiked.message)
      .responseWith[Array[CommentNotFoundType]](CommentNotFound.status.code, CommentNotFound.message)

  } { request: DeleteCommentLike =>
    commentLikesService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
