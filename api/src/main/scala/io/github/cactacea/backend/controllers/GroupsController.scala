package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.swagger.models.Swagger

import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.responses.{BadRequest, NotFound}

@Singleton
class GroupsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Groups"

  @Inject private var groupsService: GroupsService = _

  getWithDoc("/groups") { o =>
    o.summary("Search groups.")
      .tag(tagName)
      .request[GetGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetGroups =>
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

  getWithDoc("/groups/:id") { o =>
    o.summary("Get basic information about this group.")
      .tag(tagName)
      .request[GetGroup]
      .responseWith[Group](Status.Ok.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetGroup =>
    groupsService.find(
      request.id,
      SessionContext.id
    )
  }

  postWithDoc("/groups") { o =>
    o.summary("Create a group.")
      .tag(tagName)
      .request[PostGroup]
      .responseWith[GroupCreated](Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: PostGroup =>
    groupsService.create(
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      SessionContext.id
    ).map(GroupCreated(_)).map(response.created(_))
  }

  putWithDoc("/groups/:id") { o =>
    o.summary("Update this group.")
      .tag(tagName)
      .request[PutGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: PutGroup =>
    groupsService.update(
      request.id,
      request.groupName,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var groupAccountsService: GroupAccountsService = _

  postWithDoc("/groups/:id/join") { o =>
    o.summary("Join to this group,")
      .tag(tagName)
      .request[PostJoinGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[BadRequest](Status.BadRequest.code, AuthorityNotFound.message)
      .responseWith[BadRequest](Status.BadRequest.code, AccountAlreadyJoined.message)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: PostJoinGroup =>
    groupAccountsService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/groups/:id/leave") { o =>
    o.summary("Leave from this group.")
      .tag(tagName)
      .request[PostLeaveGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[BadRequest](Status.BadRequest.code, AuthorityNotFound.message)
      .responseWith[BadRequest](Status.BadRequest.code, AccountNotJoined.message)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: PostLeaveGroup =>
    groupAccountsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc("/groups/:id/accounts") { o =>
    o.summary("Get acounts list of this group.")
      .tag(tagName)
      .request[GetGroupAccounts]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: GetGroupAccounts =>
    groupAccountsService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/accounts/:account_id/groups/:group_id/join") { o =>
    o.summary("Join this account in this group.")
      .tag(tagName)
      .request[PostAccountJoinGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: PostAccountJoinGroup =>
    groupAccountsService.create(
      request.accountId,
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/accounts/:account_id/groups/:group_id/leave") { o =>
    o.summary("Leave this account from this group.")
      .tag(tagName)
      .request[PostAccountJoinGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: PostAccountLeaveGroup =>
    groupAccountsService.delete(
      request.accountId,
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var accountGroupsService: AccountGroupsService = _

  getWithDoc("/accounts/:id/group") { o =>
    o.summary("Get a direct message group to this account.")
      .tag(tagName)
      .request[GetAccountGroup]
      .responseWith[Group](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: GetAccountGroup =>
    accountGroupsService.find(
      request.id,
      SessionContext.id
    )
  }

  getWithDoc("/accounts/:id/groups") { o =>
    o.summary("Get groups list this account joined.")
      .tag(tagName)
      .request[GetAccountGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, AccountNotFound.message)

  } { request: GetAccountGroups =>
    accountGroupsService.findAll(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc("/session/groups") { o =>
    o.summary("Get groups list session account joined.")
      .tag(tagName)
      .request[GetSessionGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      true,
      SessionContext.id
    )
  }

  getWithDoc("/session/hides") { o =>
    o.summary("Get hidden groups list session account joined.")
      .tag(tagName)
      .request[GetSessionGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      false,
      SessionContext.id
    )
  }

  deleteWithDoc("/groups/:id") { o =>
    o.summary("Hide this group and delete all messages.")
      .tag(tagName)
      .request[DeleteGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)

  } { request: DeleteGroup =>
    accountGroupsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/groups/:id/hides") { o =>
    o.summary("Hide this group.")
      .tag(tagName)
      .request[PostHideGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: PostHideGroup =>
    accountGroupsService.hide(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc("/groups/:id/hides") { o =>
    o.summary("Show this group.")
      .tag(tagName)
      .request[DeleteHideGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[BadRequest](Status.BadRequest.code, validationErrorMessage)
      .responseWith[NotFound](Status.NotFound.code, GroupNotFound.message)

  } { request: DeleteHideGroup =>
    accountGroupsService.show(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}

