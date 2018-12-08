package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.CommentsService
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.comment._
import io.github.cactacea.backend.models.responses.CommentCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class CommentsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, commentsService: CommentsService, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithPermission("/comments")(Permissions.basic) { o =>
      o.summary("Search comments")
        .tag(commentsTag)
        .operationId("findComments")
        .request[GetComments]
        .responseWith[Array[Comment]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: GetComments =>
      commentsService.findAll(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    postWithPermission("/comments")(Permissions.comments) { o =>
      o.summary("Create a comment on a feed")
        .tag(commentsTag)
        .operationId("postComment")
        .request[PostComment]
        .responseWith[CommentCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: PostComment =>
      commentsService.create(
        request.id,
        request.message,
        SessionContext.id
      ).map(CommentCreated(_)).map(response.created(_))
    }

    getWithPermission("/comments/:id")(Permissions.basic) { o =>
      o.summary("Get basic information about a comment")
        .tag(commentsTag)
        .operationId("findComment")
        .request[GetComment]
        .responseWith[Comment](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: GetComment =>
      commentsService.find(
        request.id,
        SessionContext.id
      )
    }

    deleteWithPermission("/comments/:id")(Permissions.comments) { o =>
      o.summary("Remove a comment")
        .tag(commentsTag)
        .operationId("deleteComment")
        .request[DeleteComment]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: DeleteComment =>
      commentsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    postWithPermission("/comments/:id/reports")(Permissions.reports) { o =>
      o.summary("Report a comment")
        .tag(commentsTag)
        .operationId("reportComment")
        .request[PostCommentReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: PostCommentReport =>
      commentsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        SessionContext.id
      )
    }

  }

}
