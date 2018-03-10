package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.comment.PostCommentReport
import io.github.cactacea.backend.models.requests.feed.PostFeedReport
import io.github.cactacea.backend.models.requests.group.PostGroupReport
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, CommentNotFound, FeedNotFound, GroupNotFound}
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class ReportsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Reports"

  @Inject private var accountsService: AccountsService = _

  postWithDoc("/accounts/:id/reports") { o =>
    o.summary("Report a account")
      .tag(tagName)
      .request[PostAccountReport]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: PostAccountReport =>
    accountsService.report(
      request.id,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var feedsService: FeedsService = _

  postWithDoc("/feeds/:id/reports") { o =>
    o.summary("Report a feed")
      .tag(tagName)
      .request[PostFeedReport]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, FeedNotFound.message)

  } { request: PostFeedReport =>
    feedsService.report(
      request.id,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var commentsService: CommentsService = _

  postWithDoc("/comments/:id/reports") { o =>
    o.summary("Report a comment")
      .tag(tagName)
      .request[PostCommentReport]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, CommentNotFound.message)

  } { request: PostCommentReport =>
    commentsService.report(
      request.id,
      request.reportType,
      SessionContext.id
    )
  }

  @Inject private var groupsService: GroupsService = _

  postWithDoc("/groups/:id/reports") { o =>
    o.summary("Report a group")
      .tag(tagName)
      .request[PostGroupReport]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: PostGroupReport =>
    groupsService.report(
      request.id,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
