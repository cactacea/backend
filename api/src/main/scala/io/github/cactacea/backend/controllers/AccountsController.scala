package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class AccountsController extends Controller {

  @Inject private var accountsService: AccountsService = _

  get("/accounts") { request: GetAccounts =>
    accountsService.find(
      request.displayName,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  get("/accounts/:id") { request: GetAccount =>
    accountsService.find(
      request.accountId,
      SessionContext.id
    )
  }

  put("/accounts/:id") { request: PutAccount =>
    accountsService.update(
      request.accountId,
      request.displayName,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  post("/accounts/:id/reports") { request: PostAccountReport =>
    accountsService.report(
      request.accountId,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  get("/account/:account_name") { request: GetAccountName =>
    accountsService.notExist(
      request.accountName
    ).map(_ => response.noContent)
  }



  @Inject private var feedsService: FeedsService = _

  get("/accounts/:id/feeds") { request: GetFeeds =>
    feedsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  @Inject private var followsService: FollowsService = _

  get("/accounts/:id/follows") { request: GetFollows =>
    followsService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/accounts/:id/follows") { request: PostFollow =>
    followsService.create(
      request.accountId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/accounts/:id/follows") { request: DeleteFollow =>
    followsService.delete(
      request.accountId,
      SessionContext.id
    ).map(_ => response.noContent)
  }


  @Inject private var followersService: FollowersService = _

  get("/accounts/:id/followers") { request: GetFollowers =>
    followersService.find(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

}
