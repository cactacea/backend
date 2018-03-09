package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.comment.PostCommentReport
import io.github.cactacea.backend.models.requests.feed.PostFeedReport
import io.github.cactacea.backend.models.requests.group.PostGroupReport
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class ReportsController extends Controller {

  @Inject private var accountsService: AccountsService = _

  post("/accounts/:id/reports") { request: PostAccountReport =>
    accountsService.report(
      request.accountId,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var feedsService: FeedsService = _

  post("/feeds/:id/reports") { request: PostFeedReport =>
    feedsService.report(
      request.feedId,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var commentsService: CommentsService = _
  post("/comments/:id/reports") { request: PostCommentReport =>
    commentsService.report(
      request.commentId,
      request.reportType,
      SessionContext.id
    )
  }

  @Inject private var groupsService: GroupsService = _

  post("/groups/:id/reports") { request: PostGroupReport =>
    groupsService.report(
      request.groupId,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}
