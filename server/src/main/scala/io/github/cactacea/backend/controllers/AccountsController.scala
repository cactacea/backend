package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, GroupNotFound}
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.feed.GetAccountFeeds
import io.github.cactacea.backend.models.requests.group.{GetAccountGroup, GetAccountGroups}
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.auth.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class AccountsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    accountsService: AccountsService,
                                    feedsService: FeedsService,
                                    feedLikesService: FeedLikesService,
                                    followersService: FollowersService,
                                    friendsService: FriendsService,
                                    groupAccountsService: GroupAccountsService,
                                    accountGroupsService: AccountGroupsService,
                                    s: Swagger
                                  ) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/accounts") { o =>
      o.summary("Find accounts")
        .tag(sessionTag)
        .operationId("findAccounts")
        .request[GetAccounts]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)

    } { request: GetAccounts =>
      accountsService.find(
        request.accountName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/accounts/:id") { o =>
      o.summary("Get information about a account")
        .tag(accountsTag)
        .operationId("findAccount")
        .request[GetAccount]
        .responseWith[Account](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetAccount =>
      accountsService.find(
        request.id,
        CactaceaContext.id
      )
    }

    getWithDoc("/accounts/:id/status") { o =>
      o.summary("Get account on")
        .tag(accountsTag)
        .operationId("findAccountStatus")
        .request[GetAccountStatus]
        .responseWith[AccountStatus](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetAccountStatus =>
      accountsService.findAccountStatus(
        request.id,
        CactaceaContext.id
      )
    }

    putWithDoc("/accounts/:id/display_name") { o =>
      o.summary("Change display name to session account")
        .tag(accountsTag)
        .operationId("updateAccountDisplayName")
        .request[PutAccountDisplayName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: PutAccountDisplayName =>
      accountsService.update(
        request.id,
        request.displayName,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    getWithDoc("/accounts/:id/feeds") { o =>
      o.summary("Get feeds list a account posted")
        .tag(accountsTag)
        .operationId("findAccountFeeds")
        .request[GetAccountFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetAccountFeeds =>
      feedsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/accounts/:id/likes") { o =>
      o.summary("Get account's liked feeds")
        .tag(accountsTag)
        .operationId("findAccountFeedsLiked")
        .request[GetLikes]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetLikes =>
      feedLikesService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/accounts/:id/followers") { o =>
      o.summary("Get accounts list a account is followed by")
        .tag(accountsTag)
        .operationId("findAccountFollowers")
        .request[GetFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFollowers =>
      followersService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/accounts/:id/friends") { o =>
      o.summary("Get a account's friends list")
        .tag(accountsTag)
        .operationId("findAccountFriends")
        .request[GetFriends]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetFriends =>
      friendsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }


    postWithDoc("/accounts/:accountId/groups/:groupId/join") { o =>
      o.summary("Join a account in a group")
        .tag(accountsTag)
        .operationId("joinAccount")
        .request[PostAccountJoinGroup]
        .responseWith(Status.Ok.code, Status.NoContent.reason)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound, AccountNotFound))))
    } { request: PostAccountJoinGroup =>
      groupAccountsService.create(
        request.accountId,
        request.groupId,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    postWithDoc("/accounts/:accountId/groups/:groupId/leave") { o =>
      o.summary("Leave a account from a group")
        .tag(accountsTag)
        .operationId("leaveAccount")
        .request[PostAccountJoinGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound, GroupNotFound))))
    } { request: PostAccountLeaveGroup =>
      groupAccountsService.delete(
        request.accountId,
        request.groupId,
        CactaceaContext.id
      ).map(_ => response.ok)
    }


    getWithDoc("/accounts/:id/group") { o =>
      o.summary("Get a direct message group to a account")
        .tag(accountsTag)
        .operationId("findAccountGroup")
        .request[GetAccountGroup]
        .responseWith[Group](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: GetAccountGroup =>
      accountGroupsService.find(
        request.id,
        CactaceaContext.id
      )
    }

    getWithDoc("/accounts/:id/groups") { o =>
      o.summary("Get groups list a account groupJoined")
        .tag(accountsTag)
        .operationId("findAccountGroups")
        .request[GetAccountGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))

    } { request: GetAccountGroups =>
      accountGroupsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    postWithDoc("/accounts/:id/reports") { o =>
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
        CactaceaContext.id
      ).map(_ => response.ok)
    }


  }

}
