package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.group._
import io.github.cactacea.backend.models.responses.GroupCreated
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class GroupsController @Inject()(
                                  @Flag("cactacea.api.prefix") apiPrefix: String,
                                  groupsService: GroupsService,
                                  groupAccountsService: GroupAccountsService,
                                  accountGroupsService: AccountGroupsService,
                                  s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/groups") { o =>
      o.summary("Search groups")
        .tag(groupsTag)
        .operationId("searchGroups")
        .request[GetGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
    } { request: GetGroups =>
      groupsService.find(
        request.groupName,
        request.invitationOnly,
        request.groupPrivacyType,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    getWithDoc("/groups/:id") { o =>
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

    postWithDoc("/groups") { o =>
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

    putWithDoc("/groups/:id") { o =>
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


    postWithDoc("/groups/:id/join") { o =>
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

    postWithDoc("/groups/:id/leave") { o =>
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

    getWithDoc("/groups/:id/accounts") { o =>
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

    deleteWithDoc("/groups/:id") { o =>
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

    postWithDoc("/groups/:id/hides") { o =>
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

    deleteWithDoc("/groups/:id/hides") { o =>
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

    postWithDoc("/groups/:id/reports") { o =>
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

