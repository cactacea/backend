package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.CommentLikesService
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.comment._
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.auth.CactaceaContext

import io.swagger.models.Swagger

@Singleton
class CommentLikesController @Inject()(
                                        @Flag("cactacea.api.prefix") apiPrefix: String,
                                        commentLikesService: CommentLikesService,
                                        s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/comments/:id/likes") { o =>
      o.summary("Get accounts list who liked on a comment")
        .tag(commentLikesTag)
        .operationId("findAccountsLikedComment")
        .request[GetCommentLikes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Array(CommentNotFound))))
    } { request: GetCommentLikes =>
      commentLikesService.findAccounts(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    postWithDoc("/comments/:id/likes") { o =>
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
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    deleteWithDoc("/comments/:id/likes") { o =>
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
        CactaceaContext.id
      ).map(_ => response.ok)
    }

  }


}
