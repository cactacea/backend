package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services.InvitationsService
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.user.{DeleteInvitation, PostInvitations}
import io.github.cactacea.backend.server.models.requests.channel.{PostAcceptInvitation, PostRejectInvitation}
import io.github.cactacea.backend.server.models.responses.InvitationCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class InvitationsController @Inject()(
                                       @Flag("cactacea.api.prefix") apiPrefix: String,
                                       invitationService: InvitationsService,
                                       s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(invitations).postWithDoc("/invitations") { o =>
      o.summary("Create invitations")
        .tag(invitationsTag)
        .operationId("createInvitations")
        .request[PostInvitations]
        .responseWith[InvitationCreated](Status.Created.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: PostInvitations =>
      invitationService.create(
        request.userIds.toList,
        request.id,
        CactaceaContext.sessionId
      ).map(_.map(InvitationCreated(_))).map(response.created(_))
    }

    scope(invitations).deleteWithDoc("/invitations") { o =>
      o.summary("Delete a invitation ")
        .tag(usersTag)
        .operationId("deleteInvitation")
        .request[DeleteInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound, ChannelNotFound))))
    }  { request: DeleteInvitation =>
      invitationService.delete(
        request.userId,
        request.channelId,
        CactaceaContext.sessionId
      ).map(response.ok)
    }

    scope(invitations).postWithDoc("/invitations/:id/accept") { o =>
      o.summary("Accept a invitation")
        .tag(invitationsTag)
        .operationId("acceptInvitation")
        .request[PostAcceptInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserAlreadyJoined, AuthorityNotFound))))
    } { request: PostAcceptInvitation =>
      invitationService.accept(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(invitations).postWithDoc("/invitations/:id/reject") { o =>
      o.summary("Reject a invitation")
        .tag(invitationsTag)
        .operationId("rejectInvitation")
        .request[PostRejectInvitation]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(InvitationNotFound))))
    } { request: PostRejectInvitation =>
      invitationService.reject(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}
