package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.models.requests.account.{PostInvitationAccount, PostInvitationAccounts}
import io.github.cactacea.backend.models.requests.group.{GetSessionInvitations, PostAcceptInvitation, PostRejectInvitation}
import io.github.cactacea.backend.models.responses.InvitationCreated
import io.github.cactacea.backend.swagger.CactaceaDocController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
import io.github.cactacea.backend.core.application.services.GroupInvitationsService
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.swagger.models.Swagger

@Singleton
class InvitationsController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger) extends CactaceaDocController {

  protected implicit val swagger = s


  protected val tagName = "Invitations"

  @Inject private var invitationService: GroupInvitationsService = _

  prefix(apiPrefix) {

    getWithPermission("/session/invitations")(Permissions.basic) { o =>
      o.summary("Get invitations list session account received")
        .tag(tagName)
        .request[GetSessionInvitations]
        .responseWith[Array[GroupInvitation]](Status.Ok.code, successfulMessage)

    } { request: GetSessionInvitations =>
      invitationService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    postWithPermission("/invitations/:id/accept")(Permissions.groupInvitations) { o =>
      o.summary("Accept a invitation")
        .tag(tagName)
        .request[PostAcceptInvitation]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[AuthorityNotFoundType](AuthorityNotFound.status.code, AuthorityNotFound.message)
        .responseWith[AccountAlreadyJoinedType](AccountAlreadyJoined.status.code, AccountAlreadyJoined.message)
        .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

    } { request: PostAcceptInvitation =>
      invitationService.accept(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/invitations/:id/reject")(Permissions.groupInvitations) { o =>
      o.summary("Reject a invitation")
        .tag(tagName)
        .request[PostRejectInvitation]
        .responseWith(Status.NoContent.code, successfulMessage)
        .responseWith[Array[GroupInvitationNotFoundType]](GroupInvitationNotFound.status.code, GroupInvitationNotFound.message)

    } { request: PostRejectInvitation =>
      invitationService.reject(
        request.id,
        SessionContext.id
      ).map(_ => response.noContent)
    }

    postWithPermission("/groups/:id/invitations")(Permissions.groupInvitations) { o =>
      o.summary("Post a invitation to some accounts")
        .tag(tagName)
        .request[PostInvitationAccounts]
        .responseWith[InvitationCreated](Status.Ok.code, successfulMessage)
        .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

    } { request: PostInvitationAccounts =>
      invitationService.create(
        request.accountIds.toList,
        request.groupId,
        SessionContext.id
      ).map(_.map(InvitationCreated(_))).map(response.created(_))
    }

    postWithPermission("/accounts/:account_id/groups/:group_id/invitations")(Permissions.groupInvitations) { o =>
      o.summary("Create a invitation to this account")
        .tag(tagName)
        .request[PostInvitationAccount]
        .responseWith[InvitationCreated](Status.Ok.code, successfulMessage)
        .responseWith[Array[AccountNotFoundType]](AccountNotFound.status.code, AccountNotFound.message)
        .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

    }  { request: PostInvitationAccount =>
      invitationService.create(
        request.accountId,
        request.groupId,
        SessionContext.id
      ).map(InvitationCreated(_)).map(response.created(_))
    }

  }

}
