package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.GetLikes
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.models.responses.FeedCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.{Account, Feed}
import io.github.cactacea.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class FeedsController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Feeds"

  @Inject private var feedsService: FeedsService = _
  @Inject private var feedLikesService: FeedLikesService = _

  prefix(apiPrefix) {

    getWithPermission("/feeds")(Permissions.basic) { o =>
      o.summary("Search feeds")
        .tag(tagName)
        .request[GetFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetFeeds =>
      feedsService.find(
        request.since,
        request.offset,
        request.count,
        request.privacyType,
        SessionContext.id
      )
    }

    postWithPermission("/feeds")(Permissions.feeds) { o =>
      o.summary("Post a feed")
        .tag(tagName)
        .request[PostFeed]
        .responseWith[FeedCreated](Status.Created.code, successfulMessage)
        .responseWith[Array[MediumNotFoundType]](MediumNotFound.status.code, MediumNotFound.message)

    } { request: PostFeed =>
      feedsService.create(
        request.message,
        request.mediumIds.map(_.toList),
        request.tags.map(_.toList),
        request.privacyType,
        request.contentWarning,
        request.expiration,
        SessionContext.id
      ).map(FeedCreated(_)).map(response.created(_))
    }

    getWithPermission("/feeds/:id")(Permissions.basic) { o =>
      o.summary("Get basic information about this feed")
        .tag(tagName)
        .request[GetFeed]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

    } { request: GetFeed =>
      feedsService.find(
        request.id,
        SessionContext.id
      )
    }

    putWithPermission("/feeds/:id")(Permissions.feeds) { o =>
      o.summary("Update this feed")
        .tag(tagName)
        .request[PutFeed]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)
        .responseWith[Array[MediumNotFoundType]](MediumNotFound.status.code, MediumNotFound.message)

    } { request: PutFeed =>
      feedsService.edit(
        request.id,
        request.message,
        request.mediumIds.map(_.toList),
        request.tags.map(_.toList),
        request.privacyType,
        request.contentWarning,
        request.expiration,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/feeds/:id")(Permissions.feeds) { o =>
      o.summary("Delete this feed")
        .tag(tagName)
        .request[DeleteFeed]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

    } { request: DeleteFeed =>
      feedsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    getWithPermission("/accounts/:id/feeds")(Permissions.basic) { o =>
      o.summary("Get feeds list this account posted")
        .tag(tagName)
        .request[GetAccountFeeds]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetAccountFeeds =>
      feedsService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }


    getWithPermission("/feeds/:id/likes")(Permissions.basic) { o =>
      o.summary("Get accounts list who set a like to this feed")
        .tag(tagName)
        .request[GetFeedLikes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

    } { request: GetFeedLikes =>
      feedLikesService.findAccounts(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/feeds/:id/likes")(Permissions.feedLikes) { o =>
      o.summary("Set a like on this feed")
        .tag(tagName)
        .request[PostFeedLike]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[FeedAlreadyLikedType]](FeedAlreadyLiked.status.code, FeedAlreadyLiked.message)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

    } { request: PostFeedLike =>
      feedLikesService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/feeds/:id/likes")(Permissions.feeds) { o =>
      o.summary("Remove a like on this feed")
        .tag(tagName)
        .request[DeleteFeedLike]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[FeedNotLikedType]](FeedNotLiked.status.code, FeedNotLiked.message)
        .responseWith[Array[FeedNotFoundType]](FeedNotFound.status.code, FeedNotFound.message)

    } { request: DeleteFeedLike =>
      feedLikesService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    getWithPermission("/accounts/:id/likes")(Permissions.basic) { o =>
      o.summary("Get account's liked feeds")
        .tag(tagName)
        .request[GetLikes]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)

        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetLikes =>
      feedLikesService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/feeds")(Permissions.basic) { o =>
      o.summary("Get feeds list session account posted")
        .tag(tagName)
        .request[GetSessionFeeds]
        .responseWith[Feed](Status.Ok.code, successfulMessage)

    } { request: GetSessionFeeds =>
      feedsService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/likes")(Permissions.basic) { o =>
      o.summary("Get feeds list session account set a like")
        .tag(tagName)
        .request[GetSessionLikedFeeds]
        .responseWith[Feed](Status.Ok.code, successfulMessage)


    } { request: GetSessionLikedFeeds =>
      feedLikesService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

  }

}

