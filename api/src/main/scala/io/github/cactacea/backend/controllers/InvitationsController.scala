package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import io.swagger.models.Swagger
import io.github.cactacea.backend.models.requests.account.{PostInvitationAccount, PostInvitationAccounts}
import io.github.cactacea.backend.models.requests.group.{GetSessionInvitations, PostAcceptInvitation, PostRejectInvitation}
import io.github.cactacea.backend.models.responses.InvitationCreated
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.services.GroupInvitationsService
import io.github.cactacea.core.domain.models.GroupInvitation
import io.github.cactacea.core.util.auth.SessionContext
import io.github.cactacea.core.util.responses.CactaceaError.{AccountAlreadyJoined, AuthorityNotFound, GroupInvitationNotFound, GroupNotFound, _}
import io.github.cactacea.core.util.responses.NotFound

@Singleton
class InvitationsController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s

  protected val tagName = "Invitations"

  @Inject private var invitationService: GroupInvitationsService = _

  getWithDoc("/session/invitations") { o =>
    o.summary("Get invitations list session account received")
      .tag(tagName)
      .request[GetSessionInvitations]
      .responseWith[Array[GroupInvitation]](Status.Ok.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)

  } { request: GetSessionInvitations =>
    invitationService.find(
      request.since,
      request.offset,
      request.count,
      SessionContext.id
    )
  }

  postWithDoc("/invitations/:id/accept") { o =>
    o.summary("Accept a invitation")
      .tag(tagName)
      .request[PostAcceptInvitation]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)
      .responseWith[AuthorityNotFoundType](AuthorityNotFound.status.code, AuthorityNotFound.message)
      .responseWith[AccountAlreadyJoinedType](AccountAlreadyJoined.status.code, AccountAlreadyJoined.message)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostAcceptInvitation =>
    invitationService.accept(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/invitations/:id/reject") { o =>
    o.summary("Reject a invitation")
      .tag(tagName)
      .request[PostRejectInvitation]
      .responseWith(Status.NoContent.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)
      .responseWith[Array[GroupInvitationNotFoundType]](GroupInvitationNotFound.status.code, GroupInvitationNotFound.message)

  } { request: PostRejectInvitation =>
    invitationService.reject(
      request.id,
      SessionContext.id
    ).map(_ => response.noContent)
  }

  postWithDoc("/groups/:id/invitations") { o =>
    o.summary("Post a invitation to some accounts")
      .tag(tagName)
      .request[PostInvitationAccounts]
      .responseWith[InvitationCreated](Status.Ok.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)
      .responseWith[Array[GroupNotFoundType]](GroupNotFound.status.code, GroupNotFound.message)

  } { request: PostInvitationAccounts =>
    invitationService.create(
      request.accountIds.toList,
      request.groupId,
      SessionContext.id
    ).map(_.map(InvitationCreated(_))).map(response.created(_))
  }

  postWithDoc("/accounts/:account_id/groups/:group_id/invitations") { o =>
    o.summary("Create a invitation to this account")
      .tag(tagName)
      .request[PostInvitationAccount]
      .responseWith[InvitationCreated](Status.Ok.code, successfulMessage)
      .responseWith[Array[ValidationErrorType]](ValidationError.status.code, ValidationError.message)
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
