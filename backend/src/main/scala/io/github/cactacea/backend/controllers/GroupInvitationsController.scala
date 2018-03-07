package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.{PostInvitationAccount, PostInvitationAccounts}
import io.github.cactacea.backend.models.requests.group.{GetSessionInvitations, PostAcceptInvitation, PostRejectInvitation}
import io.github.cactacea.backend.models.responses.InvitationCreated
import io.github.cactacea.core.application.services.GroupInvitationsService
import io.github.cactacea.core.util.auth.SessionContext._

@Singleton
class GroupInvitationsController extends Controller {

  @Inject private var invitationService: GroupInvitationsService = _

  get("/session/invitations") { request: GetSessionInvitations =>
    invitationService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/invitations/:id/accept") { request: PostAcceptInvitation =>
    invitationService.accept(
      request.groupInvitationId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/invitations/:id/reject") { request: PostRejectInvitation =>
    invitationService.reject(
      request.groupInvitationId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/invitations") { request: PostInvitationAccounts =>
    invitationService.create(
      request.accountIds,
      request.groupId,
      request.session.id
    ).map(_.map(InvitationCreated(_))).map(response.created(_))
  }

  post("/accounts/:account_id/groups/:group_id/invitations") { request: PostInvitationAccount =>
    invitationService.create(
      request.accountId,
      request.groupId,
      request.session.id
    ).map(InvitationCreated(_)).map(response.created(_))
  }

}
