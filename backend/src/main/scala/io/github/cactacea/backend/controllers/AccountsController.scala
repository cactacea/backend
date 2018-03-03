package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.AuthUserContext._

@Singleton
class AccountsController extends Controller {

  @Inject var accountsService: AccountsService = _

  get("/accounts") { request: GetAccounts =>
    accountsService.find(
      request.displayName,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  get("/accounts/:id") { request: GetAccount =>
    accountsService.find(
      request.accountId,
      request.session.id
    )
  }

  put("/accounts/:id") { request: PutAccount =>
    accountsService.update(
      request.accountId,
      request.displayName,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/accounts/:id/reports") { request: PostAccountReport =>
    accountsService.report(
      request.accountId,
      request.reportType,
      request.session.id
    ).map(_ => response.noContent)
  }


  get("/account/:account_name") { request: GetAccountName =>
    accountsService.notExist(
      request.accountName
    ).map(_ => response.noContent)
  }




  @Inject var blocksService: BlocksService = _

  post("/accounts/:id/blocks") { request: PostBlock =>
    blocksService.create(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/blocks") { request: DeleteBlock =>
    blocksService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }




  @Inject var feedsService: FeedsService = _

  get("/accounts/:id/feeds") { request: GetFeeds =>
    feedsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }



  @Inject var feedFavoritesService: FeedFavoritesService = _

  get("/accounts/:id/favorites") { request: GetFavorites =>
    feedFavoritesService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }



  @Inject var mutesService: MutesService = _

  post("/accounts/:id/mutes") { request: PostMute =>
    mutesService.create(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/mutes") { request: DeleteMute =>
    mutesService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }



  @Inject var followsService: FollowsService = _

  get("/accounts/:id/follows") { request: GetFollows =>
    followsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/accounts/:id/follows") { request: PostFollow =>
    followsService.create(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/follows") { request: DeleteFollow =>
    followsService.delete(
      request.accountId,
      request.session.id
    ).map(_ => response.noContent)
  }


  @Inject var followersService: FollowersService = _

  get("/accounts/:id/followers") { request: GetFollowers =>
    followersService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

}
