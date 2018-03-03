package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.models.requests.account.{PostInviteAccount, PostInviteAccounts}
import io.github.cactacea.backend.models.requests.group.{GetSessionInvites, PostAcceptInvite, PostRejectInvite}
import io.github.cactacea.backend.models.responses.GroupInviteCreated
import io.github.cactacea.core.application.services.GroupInvitesService
import io.github.cactacea.core.util.auth.AuthUserContext._

@Singleton
class GroupInvitesController extends Controller {

  @Inject var invitesService: GroupInvitesService = _

  get("/session/invites") { request: GetSessionInvites =>
    invitesService.find(
      request.since,
      request.offset,
      request.count,
      request.session.id
    )
  }

  post("/invites/:id/accept") { request: PostAcceptInvite =>
    invitesService.accept(
      request.groupInviteId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/invites/:id/reject") { request: PostRejectInvite =>
    invitesService.reject(
      request.groupInviteId,
      request.session.id
    ).map(_ => response.noContent)
  }

  post("/groups/:id/invites") { request: PostInviteAccounts =>
    invitesService.create(
      request.accountIds,
      request.groupId,
      request.session.id
    ).map(_.map(GroupInviteCreated(_))).map(response.created(_))
  }

  post("/accounts/:account_id/groups/:group_id/invites") { request: PostInviteAccount =>
    invitesService.create(
      request.accountId,
      request.groupId,
      request.session.id
    ).map(GroupInviteCreated(_)).map(response.created(_))
  }

}
