package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.CommentsService
import io.github.cactacea.backend.core.domain.models.Comment
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.server.models.requests.comment.{DeleteComment, GetComment, GetComments, PostComment, PostCommentReport}
import io.github.cactacea.backend.server.models.responses.CommentCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class CommentsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, commentsService: CommentsService, s: Swagger)
  extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(comments).getWithDoc("/comments") { o =>
      o.summary("Search comments")
        .tag(commentsTag)
        .operationId("findComments")
        .request[GetComments]
        .responseWith[Array[Comment]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FeedNotFound))))
    } { request: GetComments =>
      commentsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(comments).postWithDoc("/comments") { o =>
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
        request.replyId,
        CactaceaContext.sessionId
      ).map(CommentCreated(_)).map(response.created(_))
    }

    scope(comments).getWithDoc("/comments/:id") { o =>
      o.summary("Get basic information about a comment")
        .tag(commentsTag)
        .operationId("findComment")
        .request[GetComment]
        .responseWith[Comment](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: GetComment =>
      commentsService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(comments).deleteWithDoc("/comments/:id") { o =>
      o.summary("Delete a comment")
        .tag(commentsTag)
        .operationId("deleteComment")
        .request[DeleteComment]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(CommentNotFound))))
    } { request: DeleteComment =>
      commentsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(comments && reports).postWithDoc("/comments/:id/reports") { o =>
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
        CactaceaContext.sessionId
      )
    }

  }

}
