package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.GroupInvitationsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.account.{PostInvitationAccount, PostInvitationAccounts}
import io.github.cactacea.backend.models.requests.group.{PostAcceptInvitation, PostRejectInvitation}
import io.github.cactacea.backend.models.responses.InvitationCreated
import io.github.cactacea.backend.swagger.SwaggerController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.{OAuthController, Permissions}
import io.swagger.models.Swagger

@Singleton
class InvitationsController @Inject()(
                                       @Flag("cactacea.api.prefix") apiPrefix: String,
                                       invitationService: GroupInvitationsService,
                                       s: Swagger) extends SwaggerController with OAuthController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithPermission("/groups/:id/invitations")(Permissions.groupInvitations) { o =>
      o.summary("Post a groupInvitation to some accounts")
        .tag(invitationsTag)
        .operationId("create")
        .request[PostInvitationAccounts]
        .responseWith[InvitationCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: PostInvitationAccounts =>
      invitationService.create(
        request.accountIds.toList,
        request.id,
        SessionContext.id
      ).map(_.map(InvitationCreated(_))).map(response.created(_))
    }

    postWithPermission("/accounts/:accountId/groups/:groupId/invitations")(Permissions.groupInvitations) { o =>
      o.summary("Create a groupInvitation to a account")
        .tag(accountsTag)
        .operationId("invite")
        .request[PostInvitationAccount]
        .responseWith[InvitationCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound, GroupNotFound))))
    }  { request: PostInvitationAccount =>
      invitationService.create(
        request.accountId,
        request.groupId,
        SessionContext.id
      ).map(InvitationCreated(_)).map(response.created(_))
    }

    postWithPermission("/invitations/:id/accept")(Permissions.groupInvitations) { o =>
      o.summary("Accept a groupInvitation")
        .tag(invitationsTag)
        .operationId("accept")
        .request[PostAcceptInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyJoined, AuthorityNotFound))))
    } { request: PostAcceptInvitation =>
      invitationService.accept(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    postWithPermission("/invitations/:id/reject")(Permissions.groupInvitations) { o =>
      o.summary("Reject a groupInvitation")
        .tag(invitationsTag)
        .operationId("reject")
        .request[PostRejectInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupInvitationNotFound))))
    } { request: PostRejectInvitation =>
      invitationService.reject(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
