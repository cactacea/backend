package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.group.{DeleteGroup, DeleteHideGroup, GetGroup, GetGroupAccounts, PostGroup, PostGroupReport, PostHideGroup, PostJoinGroup, PostLeaveGroup, PutGroup}
import io.github.cactacea.backend.server.models.responses.GroupCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class GroupsController @Inject()(
                                  @Flag("cactacea.api.prefix") apiPrefix: String,
                                  groupsService: GroupsService,
                                  groupAccountsService: GroupAccountsService,
                                  accountGroupsService: AccountGroupsService,
                                  s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(groups).getWithDoc("/groups/:id") { o =>
      o.summary("Get basic information about a group")
        .tag(groupsTag)
        .operationId("findGroup")
        .request[GetGroup]
        .responseWith[Group](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: GetGroup =>
      groupsService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(groups).postWithDoc("/groups") { o =>
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
        CactaceaContext.sessionId
      ).map(GroupCreated(_)).map(response.created(_))
    }

    scope(groups).putWithDoc("/groups/:id") { o =>
      o.summary("Update a group")
        .tag(groupsTag)
        .operationId("updateGroup")
        .request[PutGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: PutGroup =>
      groupsService.update(
        request.id,
        request.name,
        request.byInvitationOnly,
        request.privacyType,
        request.authorityType,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }


    scope(groups).postWithDoc("/groups/:id/join") { o =>
      o.summary("Join to a group,")
        .tag(groupsTag)
        .operationId("joinGroup")
        .request[PostJoinGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyJoined, AuthorityNotFound))))
    } { request: PostJoinGroup =>
      groupAccountsService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(groups).postWithDoc("/groups/:id/leave") { o =>
      o.summary("Leave from a group")
        .tag(groupsTag)
        .operationId("leaveGroup")
        .request[PostLeaveGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyJoined, AuthorityNotFound))))
    } { request: PostLeaveGroup =>
      groupAccountsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(groups).getWithDoc("/groups/:id/accounts") { o =>
      o.summary("Get accounts list of a group")
        .tag(groupsTag)
        .operationId("findGroupAccounts")
        .request[GetGroupAccounts]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: GetGroupAccounts =>
      groupAccountsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(groups).deleteWithDoc("/groups/:id") { o =>
      o.summary("Hide a group and delete all messages")
        .tag(groupsTag)
        .operationId("deleteGroup")
        .request[DeleteGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: DeleteGroup =>
      accountGroupsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(groups).postWithDoc("/groups/:id/hides") { o =>
      o.summary("Hide a group")
        .tag(groupsTag)
        .operationId("hideGroup")
        .request[PostHideGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: PostHideGroup =>
      accountGroupsService.hide(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(groups).deleteWithDoc("/groups/:id/hides") { o =>
      o.summary("Show a group")
        .tag(groupsTag)
        .operationId("showGroup")
        .request[DeleteHideGroup]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: DeleteHideGroup =>
      accountGroupsService.show(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(groups && reports).postWithDoc("/groups/:id/reports") { o =>
      o.summary("Report a group")
        .tag(groupsTag)
        .operationId("reportGroup")
        .request[PostGroupReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: PostGroupReport =>
      groupsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}

