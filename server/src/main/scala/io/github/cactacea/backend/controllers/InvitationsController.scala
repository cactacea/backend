package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.GroupInvitationsService
import io.github.cactacea.backend.core.util.responses.CactaceaError
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.account.{PostInvitationAccount, PostInvitationAccounts}
import io.github.cactacea.backend.models.requests.group.{PostAcceptInvitation, PostRejectInvitation}
import io.github.cactacea.backend.models.responses.InvitationCreated
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.swagger.models.Swagger

@Singleton
class InvitationsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaController {

  implicit val swagger = s

  @Inject private var invitationService: GroupInvitationsService = _

  prefix(apiPrefix) {

    postWithPermission("/invitations/:id/accept")(Permissions.groupInvitations) { o =>
      o.summary("Accept a invitation")
        .tag(invitationsTag)
        .operationId("acceptGroupInvitation")
        .request[PostAcceptInvitation]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(AuthorityNotFound, GroupNotFound))
        .responseWithArray[CactaceaError](Status.BadRequest, Array(AccountAlreadyJoined))
    } { request: PostAcceptInvitation =>
      invitationService.accept(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/invitations/:id/reject")(Permissions.groupInvitations) { o =>
      o.summary("Reject a invitation")
        .tag(invitationsTag)
        .operationId("rejectGroupInvitation")
        .request[PostRejectInvitation]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(GroupInvitationNotFound))
    } { request: PostRejectInvitation =>
      invitationService.reject(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/groups/:id/invitations")(Permissions.groupInvitations) { o =>
      o.summary("Post a invitation to some accounts")
        .tag(invitationsTag)
        .operationId("createGroupInvitationToAccounts")
        .request[PostInvitationAccounts]
        .responseWith[InvitationCreated](Status.Ok.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(GroupNotFound))
    } { request: PostInvitationAccounts =>
      invitationService.create(
        request.accountIds.toList,
        request.id,
        SessionContext.id
      ).map(_.map(InvitationCreated(_))).map(response.created(_))
    }

    postWithPermission("/accounts/:account_id/groups/:group_id/invitations")(Permissions.groupInvitations) { o =>
      o.summary("Create a invitation to this account")
        .tag(invitationsTag)
        .operationId("createGroupInvitationToAccount")
        .request[PostInvitationAccount]
        .responseWith[InvitationCreated](Status.Ok.code, successfulMessage)
        .responseWithArray[CactaceaError](Status.NotFound, Array(AccountNotFound, GroupNotFound))
    }  { request: PostInvitationAccount =>
      invitationService.create(
        request.accountId,
        request.groupId,
        SessionContext.id
      ).map(InvitationCreated(_)).map(response.created(_))
    }

  }

}
