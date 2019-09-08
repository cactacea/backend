package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.CommentLikesService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.comment.{DeleteCommentLike, GetCommentLikes, PostCommentLike}
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class CommentLikesController @Inject()(
                                        @Flag("cactacea.api.prefix") apiPrefix: String,
                                        commentLikesService: CommentLikesService,
                                        f: CactaceaAuthenticationFilterFactory,
                                        s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(comments).getWithDoc("/comments/:id/likes") { o =>
      o.summary("Get users list who liked on a comment")
        .tag(commentLikesTag)
        .operationId("findUsersLikedComment")
        .request[GetCommentLikes]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: GetCommentLikes =>
      commentLikesService.findUsers(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(commentLikes).postWithDoc("/comments/:id/likes") { o =>
      o.summary("Set a like on a comment")
        .tag(commentLikesTag)
        .operationId("likeComment")
        .request[PostCommentLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(CommentAlreadyLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: PostCommentLike =>
      commentLikesService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(commentLikes).deleteWithDoc("/comments/:id/likes") { o =>
      o.summary("Remove a like on a comment")
        .tag(commentLikesTag)
        .operationId("unlikeComment")
        .request[DeleteCommentLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(CommentNotLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: DeleteCommentLike =>
      commentLikesService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }


}
