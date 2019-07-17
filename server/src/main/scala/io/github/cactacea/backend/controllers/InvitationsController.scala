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
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.context.CactaceaContext
import io.swagger.models.Swagger

@Singleton
class InvitationsController @Inject()(
                                       @Flag("cactacea.api.prefix") apiPrefix: String,
                                       invitationService: GroupInvitationsService,
                                       s: Swagger) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    postWithDoc("/groups/:id/invitations") { o =>
      o.summary("Post a groupInvitation to some accounts")
        .tag(invitationsTag)
        .operationId("inviteAccounts")
        .request[PostInvitationAccounts]
        .responseWith[InvitationCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
    } { request: PostInvitationAccounts =>
      invitationService.create(
        request.accountIds.toList,
        request.id,
        CactaceaContext.sessionId
      ).map(_.map(InvitationCreated(_))).map(response.created(_))
    }

    postWithDoc("/accounts/:accountId/groups/:groupId/invitations") { o =>
      o.summary("Create a groupInvitation to a account")
        .tag(accountsTag)
        .operationId("inviteAccount")
        .request[PostInvitationAccount]
        .responseWith[InvitationCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound, GroupNotFound))))
    }  { request: PostInvitationAccount =>
      invitationService.create(
        request.accountId,
        request.groupId,
        CactaceaContext.sessionId
      ).map(InvitationCreated(_)).map(response.created(_))
    }

    postWithDoc("/invitations/:id/accept") { o =>
      o.summary("Accept a groupInvitation")
        .tag(invitationsTag)
        .operationId("acceptInvitation")
        .request[PostAcceptInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyJoined, AuthorityNotFound))))
    } { request: PostAcceptInvitation =>
      invitationService.accept(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    postWithDoc("/invitations/:id/reject") { o =>
      o.summary("Reject a groupInvitation")
        .tag(invitationsTag)
        .operationId("rejectInvitation")
        .request[PostRejectInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(GroupInvitationNotFound))))
    } { request: PostRejectInvitation =>
      invitationService.reject(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
