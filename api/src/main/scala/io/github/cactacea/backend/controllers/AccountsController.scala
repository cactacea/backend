package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.responses.AccountNameNotExists
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class AccountsController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Accounts"

  @Inject private var accountsService: AccountsService = _

  getWithDoc(c.rootPath + "/accounts") { o =>
    o.summary("Search accounts")
      .tag(tagName)
      .request[GetAccounts]
      .responseWith[Array[Account]](Status.Ok.code, successfulMessage)


  } { request: GetAccounts =>
    accountsService.find(
      request.displayName,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc(c.rootPath + "/accounts/:id") { o =>
    o.summary("Get information about this account")
      .tag(tagName)
      .request[GetAccount]
      .responseWith[Account](Status.Ok.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: GetAccount =>
    accountsService.find(
      request.id,
      SessionContext.id
    )
  }

  putWithDoc(c.rootPath + "/accounts/:id/display_name") { o =>
    o.summary("Change display name to session account")
      .tag(tagName)
      .request[PutAccountDisplayName]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: PutAccountDisplayName =>
    accountsService.update(
      request.id,
      request.displayName,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc(c.rootPath + "/account/:account_name") { o =>
    o.summary("Confirm account name exists")
      .tag(tagName)
      .request[GetAccountName]
      .responseWith[AccountNameNotExists](Status.Ok.code, "account name")

  } { request: GetAccountName =>
    accountsService.notExist(
      request.accountName
    ).map(r => response.ok(AccountNameNotExists(request.accountName, r)))
  }

}
