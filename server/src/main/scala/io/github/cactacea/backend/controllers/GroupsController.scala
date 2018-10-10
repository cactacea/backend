package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.{BadRequest, NotFound}
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class GroupsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  @Inject private var groupsService: GroupsService = _
  @Inject private var groupAccountsService: GroupAccountsService = _
  @Inject private var accountGroupsService: AccountGroupsService = _

  prefix(apiPrefix) {

    getWithPermission("/groups")(Permissions.basic) { o =>
      o.summary("Search groups")
        .tag(groupsTag)
        .operationId("findGroups")
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
        .tag(groupsTag)
        .operationId("findGroup")
        .request[GetGroup]
        .responseWith[Group](Status.Ok.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
    } { request: GetGroup =>
      groupsService.find(
        request.id,
        SessionContext.id
      )
    }

    postWithPermission("/groups")(Permissions.groups) { o =>
      o.summary("Create a group")
        .tag(groupsTag)
        .operationId("createGroup")
        .request[PostGroup]
        .responseWith[GroupCreated](Status.Created.code, successfulMessage)
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
        .tag(groupsTag)
        .operationId("updateGroup")
        .request[PutGroup]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
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
        .tag(groupsTag)
        .operationId("joinToGroup")
        .request[PostJoinGroup]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
        .responseWithArray[BadRequest](Status.BadRequest, Array(AccountAlreadyJoined, AuthorityNotFound))
    } { request: PostJoinGroup =>
      groupAccountsService.create(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/groups/:id/leave")(Permissions.groups) { o =>
      o.summary("Leave from this group")
        .tag(groupsTag)
        .operationId("leaveFromGroup")
        .request[PostLeaveGroup]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
        .responseWithArray[BadRequest](Status.BadRequest, Array(AccountAlreadyJoined, AuthorityNotFound))
    } { request: PostLeaveGroup =>
      groupAccountsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    getWithPermission("/groups/:id/accounts")(Permissions.basic) { o =>
      o.summary("Get accounts list of this group")
        .tag(groupsTag)
        .operationId("findGroupAccounts")
        .request[GetGroupAccounts]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
    } { request: GetGroupAccounts =>
      groupAccountsService.find(
        request.id,
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    deleteWithPermission("/groups/:id")(Permissions.groups) { o =>
      o.summary("Hide this group and delete all messages")
        .tag(groupsTag)
        .operationId("deleteGroup")
        .request[DeleteGroup]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
    } { request: DeleteGroup =>
      accountGroupsService.delete(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/groups/:id/hides")(Permissions.groups) { o =>
      o.summary("Hide this group")
        .tag(groupsTag)
        .operationId("hideGroup")
        .request[PostHideGroup]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
    } { request: PostHideGroup =>
      accountGroupsService.hide(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    deleteWithPermission("/groups/:id/hides")(Permissions.groups) { o =>
      o.summary("Show this group")
        .tag(groupsTag)
        .operationId("showGroup")
        .request[DeleteHideGroup]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
    } { request: DeleteHideGroup =>
      accountGroupsService.show(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/groups/:id/reports")(Permissions.reports) { o =>
      o.summary("Report this group")
        .tag(groupsTag)
        .operationId("reportGroup")
        .request[PostGroupReport]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[NotFound](Status.NotFound, Array(GroupNotFound))
    } { request: PostGroupReport =>
      groupsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        SessionContext.id
      ).map(_ => response.noContent)
    }

  }

}

