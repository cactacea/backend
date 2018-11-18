package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.{Account, AccountStatus, Feed, Group}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, GroupNotFound}
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.feed.GetAccountFeeds
import io.github.cactacea.backend.models.requests.group.{GetAccountGroup, GetAccountGroups}
import io.github.cactacea.backend.models.responses.AccountNameNotExists
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class AccountsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var accountsService: AccountsService = _
  @Inject private var feedsService: FeedsService = _
  @Inject private var feedLikesService: FeedLikesService = _
  @Inject private var followersService: FollowersService = _
  @Inject private var friendsService: FriendsService = _
  @Inject private var groupAccountsService: GroupAccountsService = _
  @Inject private var accountGroupsService: AccountGroupsService = _

  prefix(apiPrefix) {

    getWithPermission("/accounts")(Permissions.basic) { o =>
      o.summary("Search accounts")
        .tag(accountsTag)
        .operationId("findAccounts")
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
      o.summary("Get information about a account")
        .tag(accountsTag)
        .operationId("findAccount")
        .request[GetAccount]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetAccount =>
      accountsService.find(
        request.id,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/status")(Permissions.basic) { o =>
      o.summary("Get account on")
        .tag(accountsTag)
        .operationId("findAccountStatus")
        .request[GetAccountStatus]
        .responseWith[AccountStatus](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetAccountStatus =>
      accountsService.findAccountStatus(
        request.id,
        SessionContext.id
      )
    }

    putWithPermission("/accounts/:id/display_name")(Permissions.relationships) { o =>
      o.summary("Change display name to session account")
        .tag(accountsTag)
        .operationId("updateDisplayName")
        .request[PutAccountDisplayName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: PutAccountDisplayName =>
      accountsService.update(
        request.id,
        request.displayName,
        SessionContext.id
      ).map(_ => response.ok)
    }

    getWithPermission("/account/:accountName")(Permissions.basic) { o =>
      o.summary("Confirm account name exist")
        .tag(accountsTag)
        .operationId("existAccountName")
        .request[GetAccountName]
        .responseWith[AccountNameNotExists](Status.Ok.code, successfulMessage)
    } { request: GetAccountName =>
      accountsService.notExist(
        request.accountName
      ).map(r => response.ok(AccountNameNotExists(request.accountName, r)))
    }


    getWithPermission("/accounts/:id/feeds")(Permissions.basic) { o =>
      o.summary("Get feeds list a account posted")
        .tag(feedsTag)
        .operationId("findAccountFeeds")
        .request[GetAccountFeeds]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetAccountFeeds =>
      feedsService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/likes")(Permissions.basic) { o =>
      o.summary("Get account's liked feeds")
        .tag(feedsTag)
        .operationId("findAccountLikes")
        .request[GetLikes]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetLikes =>
      feedLikesService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/followers")(Permissions.followerList) { o =>
      o.summary("Get accounts list a account is followed by")
        .tag(followsTag)
        .operationId("findAccountFollowers")
        .request[GetFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFollowers =>
      followersService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/friends")(Permissions.followerList) { o =>
      o.summary("Get a account's friends list")
        .tag(friendsTag)
        .operationId("findAccountFriends")
        .request[GetFriends]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFriends =>
      friendsService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }


    postWithPermission("/accounts/:accountId/groups/:groupId/join")(Permissions.groups) { o =>
      o.summary("Join a account in a group")
        .tag(groupsTag)
        .operationId("joinAccountToGroup")
        .request[PostAccountJoinGroup]
        .responseWith(Status.Ok.code, Status.NoContent.reason)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound, AccountNotFound))))
    } { request: PostAccountJoinGroup =>
      groupAccountsService.create(
        request.accountId,
        request.groupId,
        SessionContext.id
      ).map(_ => response.ok)
    }

    postWithPermission("/accounts/:accountId/groups/:groupId/leave")(Permissions.groups) { o =>
      o.summary("Leave a account from a group")
        .tag(groupsTag)
        .operationId("leaveAccountFromGroup")
        .request[PostAccountJoinGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound, GroupNotFound))))
    } { request: PostAccountLeaveGroup =>
      groupAccountsService.delete(
        request.accountId,
        request.groupId,
        SessionContext.id
      ).map(_ => response.ok)
    }


    getWithPermission("/accounts/:id/group")(Permissions.basic) { o =>
      o.summary("Get a direct message group to a account")
        .tag(groupsTag)
        .operationId("findAccountGroup")
        .request[GetAccountGroup]
        .responseWith[Group](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: GetAccountGroup =>
      accountGroupsService.find(
        request.id,
        SessionContext.id
      )
    }

    getWithPermission("/accounts/:id/groups")(Permissions.basic) { o =>
      o.summary("Get groups list a account joined")
        .tag(groupsTag)
        .operationId("findAccountGroups")
        .request[GetAccountGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: GetAccountGroups =>
      accountGroupsService.findAll(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/accounts/:id/reports")(Permissions.reports) { o =>
      o.summary("Report a account")
        .tag(accountsTag)
        .operationId("reportAccount")
        .request[PostAccountReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: PostAccountReport =>
      accountsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        SessionContext.id
      ).map(_ => response.ok)
    }


  }

}
