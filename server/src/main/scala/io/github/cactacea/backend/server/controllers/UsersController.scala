package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Status
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.user._
import io.github.cactacea.backend.server.models.requests.feed.GetUserFeeds
import io.github.cactacea.backend.server.models.requests.channel.{GetUserChannel, GetUserChannels}
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class UsersController @Inject()(
                                 @Flag("cactacea.api.prefix") apiPrefix: String,
                                 usersService: UsersService,
                                 feedsService: FeedsService,
                                 feedLikesService: FeedLikesService,
                                 followersService: FollowersService,
                                 friendsService: FriendsService,
                                 channelUsersService: ChannelUsersService,
                                 userChannelsService: UserChannelsService,
                                 f: CactaceaAuthenticationFilterFactory,
                                 s: Swagger
                                  ) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/users") { o =>
      o.summary("Find users")
        .tag(sessionTag)
        .operationId("findUsers")
        .request[GetUsers]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)

    } { request: GetUsers =>
      usersService.find(
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/users/:id") { o =>
      o.summary("Get information about an user")
        .tag(usersTag)
        .operationId("findUser")
        .request[GetUser]
        .responseWith[User](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetUser =>
      usersService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/users/:id/status") { o =>
      o.summary("Get user on")
        .tag(usersTag)
        .operationId("findUserStatus")
        .request[GetUserStatus]
        .responseWith[UserStatus](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetUserStatus =>
      usersService.findUserStatus(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(basic).putWithDoc("/users/:id/display_name") { o =>
      o.summary("Change display name to session user")
        .tag(usersTag)
        .operationId("updateUserDisplayName")
        .request[PutUserDisplayName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))

    } { request: PutUserDisplayName =>
      usersService.updateDisplayName(
        request.id,
        request.displayName,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(feeds).getWithDoc("/users/:id/feeds") { o =>
      o.summary("Get feeds list an user posted")
        .tag(usersTag)
        .operationId("findUserFeeds")
        .request[GetUserFeeds]
        .responseWith[Seq[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetUserFeeds =>
      feedsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/users/:id/likes") { o =>
      o.summary("Get user's liked feeds")
        .tag(usersTag)
        .operationId("findUserFeedsLiked")
        .request[GetLikes]
        .responseWith[Seq[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetLikes =>
      feedLikesService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(followerList).getWithDoc("/users/:id/followers") { o =>
      o.summary("Get users list an user is followed by")
        .tag(usersTag)
        .operationId("findUserFollowers")
        .request[GetFollowers]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetFollowers =>
      followersService.find(
        request.id,
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(relationships).getWithDoc("/users/:id/friends") { o =>
      o.summary("Get an user's friends list")
        .tag(usersTag)
        .operationId("findUserFriends")
        .request[GetFriends]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetFriends =>
      friendsService.find(
        request.id,
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }


    scope(channels).postWithDoc("/users/:userId/channels/:channelId/join") { o =>
      o.summary("Join an user in a channel")
        .tag(usersTag)
        .operationId("joinUser")
        .request[PostJoinUserChannel]
        .responseWith(Status.Ok.code, Status.NoContent.reason)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(ChannelNotFound, UserNotFound))))
    } { request: PostJoinUserChannel =>
      channelUsersService.create(
        request.userId,
        request.channelId,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(channels).postWithDoc("/users/:userId/channels/:channelId/leave") { o =>
      o.summary("Leave an user from a channel")
        .tag(usersTag)
        .operationId("leaveUser")
        .request[PostJoinUserChannel]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound, ChannelNotFound))))
    } { request: PostLeaveUserChannel =>
      channelUsersService.delete(
        request.userId,
        request.channelId,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }


    scope(channels).getWithDoc("/users/:id/channel") { o =>
      o.summary("Get a direct message channel to an user")
        .tag(usersTag)
        .operationId("findUserChannel")
        .request[GetUserChannel]
        .responseWith[Channel](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))

    } { request: GetUserChannel =>
      userChannelsService.find(
        request.id,
        CactaceaContext.sessionId
      )
    }

    scope(channels).getWithDoc("/users/:id/channels") { o =>
      o.summary("Get channels list an user channelJoined")
        .tag(usersTag)
        .operationId("findUserChannels")
        .request[GetUserChannels]
        .responseWith[Seq[Channel]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))

    } { request: GetUserChannels =>
      userChannelsService.find(
        request.id,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(reports).postWithDoc("/users/:id/reports") { o =>
      o.summary("Report an user")
        .tag(usersTag)
        .operationId("reportUser")
        .request[PostUserReport]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))

    } { request: PostUserReport =>
      usersService.report(
        request.id,
        request.reportType,
        request.reportContent,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }


  }

}
