package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.responses.AccountNameNotExists
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, AccountNotFoundType}
import io.swagger.models.Swagger

@Singleton
class AccountsController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Accounts"

  @Inject private var accountsService: AccountsService = _

  prefix(apiPrefix) {

    getWithPermission("/accounts")(Permissions.basic) { o =>
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

    getWithPermission("/accounts/:id")(Permissions.basic) { o =>
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

    getWithPermission("/accounts/:id/status")(Permissions.basic) { o =>
      o.summary("Get account on")
        .tag(tagName)
        .request[GetAccountStatus]
        .responseWith[AccountStatus](Status.Ok.code, successfulMessage)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

    } { request: GetAccountStatus =>
      accountsService.findAccountStatus(
        request.id,
        SessionContext.id
      )
    }

    putWithPermission("/accounts/:id/display_name")(Permissions.relationships) { o =>
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

    getWithPermission("/account/:account_name")(Permissions.basic) { o =>
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


}
