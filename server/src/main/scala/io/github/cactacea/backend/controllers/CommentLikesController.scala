package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.CommentLikesService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.comment._
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class CommentLikesController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var commentLikesService: CommentLikesService = _

  prefix(apiPrefix) {

    getWithPermission("/comments/:id/likes")(Permissions.basic) { o =>
      o.summary("Get accounts list who liked on a comment")
        .tag(commentsTag)
        .operationId("findCommentLikes")
        .request[GetCommentLikes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Array(CommentNotFound))))
    } { request: GetCommentLikes =>
      commentLikesService.findAccounts(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/comments/:id/likes")(Permissions.commentLikes) { o =>
      o.summary("Set a like on a comment")
        .tag(commentsTag)
        .operationId("likeComment")
        .request[PostCommentLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(CommentAlreadyLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: PostCommentLike =>
      commentLikesService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    deleteWithPermission("/comments/:id/likes")(Permissions.comments) { o =>
      o.summary("Remove a like on a comment")
        .tag(commentsTag)
        .operationId("unlikeComment")
        .request[DeleteCommentLike]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(CommentNotLiked))))
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: DeleteCommentLike =>
      commentLikesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }


}
