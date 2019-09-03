package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.channel._
import io.github.cactacea.backend.server.models.responses.ChannelCreated
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class ChannelsController @Inject()(
                                    @Flag("cactacea.api.prefix") apiPrefix: String,
                                    channelsService: ChannelsService,
                                    channelUsersService: ChannelUsersService,
                                    userChannelsService: UserChannelsService,
                                    s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(channels).getWithDoc("/channels/:id") { o =>
      o.summary("Get basic information about a channel")
        .tag(channelsTag)
        .operationId("findChannel")
        .request[GetChannel]
        .responseWith[Channel](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: GetChannel =>
      channelsService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(channels).postWithDoc("/channels") { o =>
      o.summary("Create a channel")
        .tag(channelsTag)
        .operationId("createChannel")
        .request[PostChannel]
        .responseWith[ChannelCreated](Status.Created.code, successfulMessage)
    } { request: PostChannel =>
      channelsService.create(
        request.name,
        request.byInvitationOnly,
        request.privacyType,
        request.authorityType,
        CactaceaContext.sessionId
      ).map(ChannelCreated(_)).map(response.created(_))
    }

    scope(channels).putWithDoc("/channels/:id") { o =>
      o.summary("Update a channel")
        .tag(channelsTag)
        .operationId("updateChannel")
        .request[PutChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: PutChannel =>
      channelsService.update(
        request.id,
        request.name,
        request.byInvitationOnly,
        request.privacyType,
        request.authorityType,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }


    scope(channels).postWithDoc("/channels/:id/join") { o =>
      o.summary("Join to a channel,")
        .tag(channelsTag)
        .operationId("joinChannel")
        .request[PostJoinChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserAlreadyJoined, AuthorityNotFound))))
    } { request: PostJoinChannel =>
      channelUsersService.create(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(channels).postWithDoc("/channels/:id/leave") { o =>
      o.summary("Leave from a channel")
        .tag(channelsTag)
        .operationId("leaveChannel")
        .request[PostLeaveChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(UserAlreadyJoined, AuthorityNotFound))))
    } { request: PostLeaveChannel =>
      channelUsersService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(channels).getWithDoc("/channels/:id/users") { o =>
      o.summary("Get users list of a channel")
        .tag(channelsTag)
        .operationId("findChannelUsers")
        .request[GetChannelUsers]
        .responseWith[Array[Channel]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: GetChannelUsers =>
      channelUsersService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(channels).deleteWithDoc("/channels/:id") { o =>
      o.summary("Hide a channel and delete all messages")
        .tag(channelsTag)
        .operationId("deleteChannel")
        .request[DeleteChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: DeleteChannel =>
      userChannelsService.delete(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(channels).postWithDoc("/channels/:id/hides") { o =>
      o.summary("Hide a channel")
        .tag(channelsTag)
        .operationId("hideChannel")
        .request[PostHideChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: PostHideChannel =>
      userChannelsService.hide(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(channels).deleteWithDoc("/channels/:id/hides") { o =>
      o.summary("Show a channel")
        .tag(channelsTag)
        .operationId("showChannel")
        .request[DeleteHideChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: DeleteHideChannel =>
      userChannelsService.show(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(channels && reports).postWithDoc("/channels/:id/reports") { o =>
      o.summary("Report a channel")
        .tag(channelsTag)
        .operationId("reportChannel")
        .request[PostChannelReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound))))
    } { request: PostChannelReport =>
      channelsService.report(
        request.id,
        request.reportType,
        request.reportContent,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

  }

}

