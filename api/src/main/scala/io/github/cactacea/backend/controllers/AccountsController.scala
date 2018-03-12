package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.swagger.models.Swagger
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.responses.AccountNameNotExists
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.BadRequest

@Singleton
class AccountsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Accounts"

  @Inject private var accountsService: AccountsService = _

  getWithDoc("/accounts") { o =>
    o.summary("Get accounts list")
      .tag(tagName)
      .request[GetAccounts]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

  } { request: GetAccounts =>
    accountsService.find(
      request.displayName,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc("/accounts/:id") { o =>
    o.summary("Get a account")
      .tag(tagName)
      .request[GetAccount]
      .responseWith[Account](Status.Ok.code, successfulMessage)
      .responseWith[Array[BadRequest]](Status.BadRequest.code, validationErrorMessage)

  } { request: GetAccount =>
    accountsService.find(
      request.id,
      SessionContext.id
    )
  }

  putWithDoc("/accounts/:id/display_name") { o =>
    o.summary("Change account display name")
      .tag(tagName)
      .request[PutAccountDisplayName]
      .responseWith(Status.NoContent.code, successfulMessage)

  } { request: PutAccountDisplayName =>
    accountsService.update(
      request.id,
      request.displayName,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc("/account/:account_name") { o =>
    o.summary("Check a account name exist")
      .tag(tagName)
      .request[GetAccountName]
      .responseWith[AccountNameNotExists](Status.Ok.code, "account name")

  } { request: GetAccountName =>
    accountsService.notExist(
      request.accountName
    ).map(r => response.ok(AccountNameNotExists(request.accountName, r)))
  }

}
