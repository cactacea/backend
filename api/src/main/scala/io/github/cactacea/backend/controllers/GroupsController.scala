package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.swagger.models.Swagger
import io.github.cactacea.backend.models.requests.account._
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.github.cactacea.core.application.services._
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaErrors._

@Singleton
class GroupsController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Groups"

  @Inject private var groupsService: GroupsService = _

  getWithDoc(c.rootPath + "/groups") { o =>
    o.summary("Search groups")
      .tag(tagName)
      .request[GetGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)


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

  getWithDoc(c.rootPath + "/groups/:id") { o =>
    o.summary("Get basic information about this group")
      .tag(tagName)
      .request[GetGroup]
      .responseWith[Group](Status.Ok.code, successfulMessage)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)


  } { request: GetGroup =>
    groupsService.find(
      request.id,
      SessionContext.id
    )
  }

  postWithDoc(c.rootPath + "/groups") { o =>
    o.summary("Create a group")
      .tag(tagName)
      .request[PostGroup]
      .responseWith[GroupCreated](Status.NoContent.code, successfulMessage)


  } { request: PostGroup =>
    groupsService.create(
      request.name,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      SessionContext.id
    ).map(GroupCreated(_)).map(response.created(_))
  }

  putWithDoc(c.rootPath + "/groups/:id") { o =>
    o.summary("Update this group")
      .tag(tagName)
      .request[PutGroup]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)


  } { request: PutGroup =>
    groupsService.update(
      request.id,
      request.name,
      request.byInvitationOnly,
      request.privacyType,
      request.authorityType,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var groupAccountsService: GroupAccountsService = _

  postWithDoc(c.rootPath + "/groups/:id/join") { o =>
    o.summary("Join to this group,")
      .tag(tagName)
      .request[PostJoinGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[AuthorityNotFoundType](AuthorityNotFound.status.code, AuthorityNotFound.message)
      .responseWith[AccountAlreadyJoinedType](AccountAlreadyJoined.status.code, AccountAlreadyJoined.message)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostJoinGroup =>
    groupAccountsService.create(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc(c.rootPath + "/groups/:id/leave") { o =>
    o.summary("Leave from this group")
      .tag(tagName)
      .request[PostLeaveGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[AuthorityNotFoundType](AuthorityNotFound.status.code, AuthorityNotFound.message)
      .responseWith[AccountNotJoinedType](AccountNotJoined.status.code, AccountNotJoined.message)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostLeaveGroup =>
    groupAccountsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  getWithDoc(c.rootPath + "/groups/:id/accounts") { o =>
    o.summary("Get acounts list of this group")
      .tag(tagName)
      .request[GetGroupAccounts]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)

      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: GetGroupAccounts =>
    groupAccountsService.find(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc(c.rootPath + "/accounts/:account_id/groups/:group_id/join") { o =>
    o.summary("Join this account in this group")
      .tag(tagName)
      .request[PostAccountJoinGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostAccountJoinGroup =>
    groupAccountsService.create(
      request.accountId,
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc(c.rootPath + "/accounts/:account_id/groups/:group_id/leave") { o =>
    o.summary("Leave this account from this group")
      .tag(tagName)
      .request[PostAccountJoinGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostAccountLeaveGroup =>
    groupAccountsService.delete(
      request.accountId,
      request.groupId,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  @Inject private var accountGroupsService: AccountGroupsService = _

  getWithDoc(c.rootPath + "/accounts/:id/group") { o =>
    o.summary("Get a direct message group to this account")
      .tag(tagName)
      .request[GetAccountGroup]
      .responseWith[Group](Status.Ok.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: GetAccountGroup =>
    accountGroupsService.find(
      request.id,
      SessionContext.id
    )
  }

  getWithDoc(c.rootPath + "/accounts/:id/groups") { o =>
    o.summary("Get groups list this account joined")
      .tag(tagName)
      .request[GetAccountGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)

      .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)

  } { request: GetAccountGroups =>
    accountGroupsService.findAll(
      request.id,
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  getWithDoc(c.rootPath + "/session/groups") { o =>
    o.summary("Get groups list session account joined")
      .tag(tagName)
      .request[GetSessionGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)


  } { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      true,
      SessionContext.id
    )
  }

  getWithDoc(c.rootPath + "/session/hides") { o =>
    o.summary("Get hidden groups list session account joined")
      .tag(tagName)
      .request[GetSessionGroups]
      .responseWith[Array[Group]](Status.Ok.code, successfulMessage)


  } { request: GetSessionGroups =>
    accountGroupsService.findAll(
      request.since,
      request.offset,
      request.count,
      false,
      SessionContext.id
    )
  }

  deleteWithDoc(c.rootPath + "/groups/:id") { o =>
    o.summary("Hide this group and delete all messages")
      .tag(tagName)
      .request[DeleteGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: DeleteGroup =>
    accountGroupsService.delete(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc(c.rootPath + "/groups/:id/hides") { o =>
    o.summary("Hide this group")
      .tag(tagName)
      .request[PostHideGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostHideGroup =>
    accountGroupsService.hide(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  deleteWithDoc(c.rootPath + "/groups/:id/hides") { o =>
    o.summary("Show this group")
      .tag(tagName)
      .request[DeleteHideGroup]
      .responseWith(Status.NoContent.code, successfulMessage)

      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: DeleteHideGroup =>
    accountGroupsService.show(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

}

