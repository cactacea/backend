package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account.{GetFeeds, GetLikes}
import io.github.cactacea.backend.models.requests.feed._
import io.github.cactacea.backend.models.responses.FeedCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Feed
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, FeedAlreadyLiked, FeedNotFound, FeedNotLiked}
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}
import io.swagger.models.Swagger

@Singleton
class FeedsController  @Inject()(s: Swagger)extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Feeds"

  @Inject private var feedsService: FeedsService = _

  getWithDoc("/feeds") { o =>
    o.summary("Get feeds")
      .tag(tagName)
      .request[GetFeeds]
      .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
      .responseWith(Status.BadRequest.code, validationErrorMessage)

  } { request: GetFeeds =>
    feedsService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/feeds") { o =>
    o.summary("Post a feed")
      .tag(tagName)
      .request[PostFeed]
      .responseWith[FeedCreated](Status.Created.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: PostFeed =>
    feedsService.create(
      request.feedMessage,
      request.mediumIds.map(_.toList),
      request.tags.map(_.toList),
      request.privacyType,
      request.contentWarning,
      request.expiration,
      SessionContext.id
    ).map(FeedCreated(_)).map(response.created(_))
  }

  getWithDoc("/feeds/:id") { o =>
    o.summary("Get a feed")
      .tag(tagName)
      .request[GetFeed]
      .responseWith[Feed](Status.Ok.code, successfulMessage)
      .responseWith(Status.NotFound.code, FeedNotFound.message)

  } { request: GetFeed =>
    feedsService.find(
      request.id,
      SessionContext.id
    )
  }

  putWithDoc("/feeds/:id") { o =>
    o.summary("Update a feed")
      .tag(tagName)
      .request[PutFeed]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith(Status.NotFound.code, FeedNotFound.message)

  } { request: PutFeed =>
    feedsService.edit(
      request.id,
      request.feedMessage,
      request.mediumIds.map(_.toList),
      request.tags.map(_.toList),
      request.privacyType,
      request.contentWarning,
      request.expiration,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/feeds/:id") { o =>
    o.summary("Delete a feed")
      .tag(tagName)
      .request[DeleteFeed]
      .responseWith(Status.NoContent.code, successfulMessage)

  } { request: DeleteFeed =>
    feedsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc("/accounts/:id/feeds") { o =>
    o.summary("Get a account's feeds")
      .tag(tagName)
      .request[GetFeeds]
      .responseWith[Feed](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: GetFeeds =>
    feedsService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  @Inject private var feedLikesService: FeedLikesService = _

  getWithDoc("/feeds/:id/likes") { o =>
    o.summary("Get a feed likes")
      .tag(tagName)
      .request[GetFeedLikes]
      .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith(Status.NotFound.code, FeedNotFound.message)

  } { request: GetFeedLikes =>
    feedLikesService.findAccounts(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/feeds/:id/likes") { o =>
    o.summary("Like a feed")
      .tag(tagName)
      .request[PostFeedLike]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith(Status.BadRequest.code, FeedAlreadyLiked.message)
      .responseWith[NotFound](Status.NotFound.code, FeedNotFound.message)

  } { request: PostFeedLike =>
    feedLikesService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/feeds/:id/likes") { o =>
    o.summary("Unlike a feed")
      .tag(tagName)
      .request[DeleteFeedLike]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith(Status.BadRequest.code, FeedNotLiked.message)
      .responseWith(Status.NotFound.code, FeedNotFound.message)

  } { request: DeleteFeedLike =>
    feedLikesService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc("/accounts/:id/likes") { o =>
    o.summary("Get account's liked feeds")
      .tag(tagName)
      .request[GetLikes]
      .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: GetLikes =>
    feedLikesService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc("/session/feeds") { o =>
    o.summary("Get session's feeds")
      .tag(tagName)
      .request[GetSessionFeeds]
      .responseWith[Feed](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionFeeds =>
    feedsService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc("/session/likes") { o =>
    o.summary("Get session's liked feeds")
      .tag(tagName)
      .request[GetSessionLikedFeeds]
      .responseWith[Feed](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionLikedFeeds =>
    feedLikesService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}

