package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.feed.{GetSessionFavoriteFeeds, GetSessionFeeds}
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class SessionController extends Controller {

  @Inject private var accountsService: AccountsService = _
  @Inject private var sessionService: SessionsService = _

  get("/session") { request: GetSessionAccount =>
    accountsService.find(
      request.session.id
    )
  }

  delete("/session") { request: Request =>
    sessionService.signOut(
      request.udid,
      request.id
    ).map(_ => response.noContent)
  }


  put("/session/account_name") { request: PutSessionAccountName =>
    accountsService.update(
      request.accountName,
      request.session.id
    ).map(_ => response.noContent)
  }

  put("/session/password") { request: PutSessionPassword =>
    accountsService.update(
      request.oldPassword,
      request.newPassword,
      request.session.id
    ).map(_ => response.noContent)
  }

  put("/session/profile") { request: PutSessionProfile =>
    accountsService.update(
      request.displayName,
      request.web,
      request.birthday,
      request.location,
      request.bio,
      request.session.id
    ).map(_ => response.noContent)
  }

  put("/session/profile_image") { request: PutSessionProfileImage =>
    accountsService.update(
      request.mediumId,
      request.session.id
    ).map(_ => response.noContent)
  }

  @Inject private var followsService: FollowsService = _
  @Inject private var followersService: FollowersService = _
  @Inject private var friendsService: FriendsService = _

  get("/session/follows") { request: GetSessionFollows =>
    followsService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  get("/session/followers") { request: GetSessionFollowers =>
    followersService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  get("/session/friends") { request: GetSessionFriends =>
    friendsService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  @Inject private var feedsService: FeedsService = _

  get("/session/feeds") { request: GetSessionFeeds =>
    feedsService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  @Inject private var feedFavoritesService: FeedFavoritesService = _

  get("/session/favorites") { request: GetSessionFavoriteFeeds =>
    feedFavoritesService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

}
