package io.github.cactacea.backend.controllers

import com.google.inject.Inject
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.util.auth.AuthUserContext._

class GroupsController extends Controller {

  @Inject var groupsService: GroupsService = _

  get("/groups") { request: GetGroups =>
    groupsService.find(
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  get("/groups/:id") { request: GetGroup =>
    groupsService.find(
      request.groupId,
      request.session.id
    )
  }

  post("/groups") { request: PostGroup =>
    groupsService.create(
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      request.session.id
    ).map(GroupCreated(_)).map(response.created(_))
  }

  put("/groups/:id") { request: PutGroup =>
    groupsService.update(
      request.groupId,
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/reports") { request: PostGroupReport =>
    groupsService.report(
      request.groupId,
      request.reportType,
      request.session.id
    ).map(_ => response.noContent)
  }



  @Inject var groupAccountsService: GroupAccountsService = _

  post("/groups/:id/joins") { request: PostJoinGroup =>
    groupAccountsService.create(
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/leaves") { request: PostLeaveGroup =>
    groupAccountsService.delete(
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

  get("/groups/:id/accounts") { request: GetGroupAccounts =>
    groupAccountsService.find(
      request.groupId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/accounts/:account_id/groups/:group_id/joins") { request: PostAccountJoinGroup =>
    groupAccountsService.create(
      request.accountId,
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/accounts/:account_id/groups/:group_id/leaves") { request: PostAccountLeaveGroup =>
    groupAccountsService.delete(
      request.accountId,
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

  @Inject var accountGroupsService: AccountGroupsService = _

  get("/accounts/:id/group") { request: GetAccountGroup =>
    accountGroupsService.find(
      request.accountId,
      request.session.id
    )
  }

  get("/accounts/:id/groups") { request: GetAccountGroups =>
    accountGroupsService.findAll(
      request.accountId,
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  get("/session/groups") { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      true,
      request.session.id
    )
  }

  get("/session/hides") { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      false,
      request.session.id
    )
  }

  delete("/groups/:id") { request: DeleteGroup =>
    accountGroupsService.delete(
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/hides") { request: PostHideGroup =>
    accountGroupsService.hide(
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

  delete("/groups/:id/hides") { request: DeleteHideGroup =>
    accountGroupsService.show(
      request.groupId,
      request.session.id
    ).map(_ => response.noContent)
  }

}

