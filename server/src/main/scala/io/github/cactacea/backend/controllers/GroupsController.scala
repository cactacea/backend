package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.{PostAccountJoinGroup, PostAccountLeaveGroup}
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.backend.util.auth.SessionContext
import io.github.cactacea.backend.util.oauth.Permissions
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class GroupsController @Inject()(@Flag("api.prefix") apiPrefix: String, s: Swagger) extends BackendController {

  protected implicit val swagger = s


  protected val tagName = "Groups"

  @Inject private var groupsService: GroupsService = _
  @Inject private var groupAccountsService: GroupAccountsService = _
  @Inject private var accountGroupsService: AccountGroupsService = _

  prefix(apiPrefix) {

    getWithPermission("/groups")(Permissions.basic) { o =>
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

    getWithPermission("/groups/:id")(Permissions.basic) { o =>
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

    postWithPermission("/groups")(Permissions.groups) { o =>
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

    putWithPermission("/groups/:id")(Permissions.groups) { o =>
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


    postWithPermission("/groups/:id/join")(Permissions.groups) { o =>
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

    postWithPermission("/groups/:id/leave")(Permissions.groups) { o =>
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

    getWithPermission("/groups/:id/accounts")(Permissions.basic) { o =>
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

    postWithPermission("/accounts/:account_id/groups/:group_id/join")(Permissions.groups) { o =>
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

    postWithPermission("/accounts/:account_id/groups/:group_id/leave")(Permissions.groups) { o =>
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


    getWithPermission("/accounts/:id/group")(Permissions.basic) { o =>
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

    getWithPermission("/accounts/:id/groups")(Permissions.basic) { o =>
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

    getWithPermission("/session/groups")(Permissions.basic) { o =>
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

    getWithPermission("/session/hides")(Permissions.basic) { o =>
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

    deleteWithPermission("/groups/:id")(Permissions.groups) { o =>
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

    postWithPermission("/groups/:id/hides")(Permissions.groups) { o =>
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

    deleteWithPermission("/groups/:id/hides")(Permissions.groups) { o =>
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

}

