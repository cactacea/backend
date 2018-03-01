package io.github.cactacea.backend.controllers

import com.google.inject.Inject
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.workertier._
import io.github.cactacea.core.application.services._

class WorkerTierController extends Controller {

  @Inject var commentsService: CommentsService = _
  @Inject var messageService: MessagesService = _
  @Inject var feedsService: FeedsService = _
  @Inject var feedFavoritesService: FeedFavoritesService = _
  @Inject var groupInvitesService: GroupInvitesService = _

  get("/messages/:id/deliver") { request: GetMessageDelivery =>
    messageService.deliver(request.messageId).map(_ => response.noContent)
  }

  get("/feeds/:id/deliver") { request: GetFeedDelivery =>
    feedsService.deliver(request.feedId).map(_ => response.noContent)
  }

  get("/messages/:id/notice") { request: GetMessageDelivery =>
    messageService.notice(request.messageId).map(_ => response.noContent)
  }

  get("/feeds/:id/notice") { request: GetFeedDelivery =>
    feedsService.notice(request.feedId).map(_ => response.noContent)
  }

  get("/feeds/:id/favorite/notice") { request: GetFeedFavoriteDelivery =>
    feedFavoritesService.notice(request.feedId).map(_ => response.noContent)
  }

  get("/comments/:id/notice") { request: GetCommentDelivery =>
    commentsService.notice(request.commentId).map(_ => response.noContent)
  }

  get("/invites/:id/notice") { request: GetGroupInviteDelivery =>
    groupInvitesService.notice(request.groupInviteId).map(_ => response.noContent)
  }

}
