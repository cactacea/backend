package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.PostAccountReport
import io.github.cactacea.backend.models.requests.comment.PostCommentReport
import io.github.cactacea.backend.models.requests.feed.PostFeedReport
import io.github.cactacea.backend.models.requests.group.PostGroupReport
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger


@Singleton
class ReportsController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Reports"

  @Inject private var accountsService: AccountsService = _
  @Inject private var feedsService: FeedsService = _
  @Inject private var commentsService: CommentsService = _
  @Inject private var groupsService: GroupsService = _

  prefix(apiPrefix) {

    postWithPermission("/accounts/:id/reports")(Permissions.reports) { o =>
      o.summary("Report this account")
        .tag(tagName)
        .request[PostAccountReport]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: PostAccountReport =>
      accountsService.report(
        request.id,
        request.reportType,
        SessionContext.id
      ).map(_ => response.noContent)
    }


    postWithPermission("/feeds/:id/reports")(Permissions.reports) { o =>
      o.summary("Report this feed")
        .tag(tagName)
        .request[PostFeedReport]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

    } { request: PostFeedReport =>
      feedsService.report(
        request.id,
        request.reportType,
        SessionContext.id
      ).map(_ => response.noContent)
    }


    postWithPermission("/comments/:id/reports")(Permissions.reports) { o =>
      o.summary("Report this comment")
        .tag(tagName)
        .request[PostCommentReport]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[CommentNotFoundType]](CommentNotFound.status.code, CommentNotFound.message)

    } { request: PostCommentReport =>
      commentsService.report(
        request.id,
        request.reportType,
        SessionContext.id
      )
    }


    postWithPermission("/groups/:id/reports")(Permissions.reports) { o =>
      o.summary("Report this group")
        .tag(tagName)
        .request[PostGroupReport]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

    } { request: PostGroupReport =>
      groupsService.report(
        request.id,
        request.reportType,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}
