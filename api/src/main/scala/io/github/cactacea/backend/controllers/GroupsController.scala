package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.SessionContext

@Singleton
class GroupsController extends Controller {

  @Inject private var groupsService: GroupsService = _

  get("/groups") { request: GetGroups =>
    groupsService.find(
      request.groupName,
      request.invitationOnly,
      request.privacyType,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  get("/groups/:id") { request: GetGroup =>
    groupsService.find(
      request.groupId,
      SessionContext.id
    )
  }

  post("/groups") { request: PostGroup =>
    groupsService.create(
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      SessionContext.id
    ).map(GroupCreated(_)).map(response.created(_))
  }

  put("/groups/:id") { request: PutGroup =>
    groupsService.update(
      request.groupId,
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/reports") { request: PostGroupReport =>
    groupsService.report(
      request.groupId,
      request.reportType,
      SessionContext.id
    ).map(_ => response.noContent)
  }



  @Inject private var groupAccountsService: GroupAccountsService = _

  post("/groups/:id/join") { request: PostJoinGroup =>
    groupAccountsService.create(
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/leave") { request: PostLeaveGroup =>
    groupAccountsService.delete(
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  get("/groups/:id/accounts") { request: GetGroupAccounts =>
    groupAccountsService.find(
      request.groupId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  post("/accounts/:account_id/groups/:group_id/join") { request: PostAccountJoinGroup =>
    groupAccountsService.create(
      request.accountId,
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  post("/accounts/:account_id/groups/:group_id/leave") { request: PostAccountLeaveGroup =>
    groupAccountsService.delete(
      request.accountId,
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var accountGroupsService: AccountGroupsService = _

  get("/accounts/:id/group") { request: GetAccountGroup =>
    accountGroupsService.find(
      request.accountId,
      SessionContext.id
    )
  }

  get("/accounts/:id/groups") { request: GetAccountGroups =>
    accountGroupsService.findAll(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  get("/session/groups") { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      true,
      SessionContext.id
    )
  }

  get("/session/hides") { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      false,
      SessionContext.id
    )
  }

  delete("/groups/:id") { request: DeleteGroup =>
    accountGroupsService.delete(
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/hides") { request: PostHideGroup =>
    accountGroupsService.hide(
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  delete("/groups/:id/hides") { request: DeleteHideGroup =>
    accountGroupsService.show(
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}

