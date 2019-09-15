package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.enums.TweetType
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.channel.{GetSessionChannels, GetSessionInvitations}
import io.github.cactacea.backend.server.models.requests.tweet.{GetSessionTweets, GetSessionLikedTweets}
import io.github.cactacea.backend.server.models.requests.session.{DeleteSignOut, _}
import io.github.cactacea.backend.server.models.requests.user.GetUserName
import io.github.cactacea.backend.server.models.responses.UserNameNotExists
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger


@Singleton
class SessionController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   usersService: UsersService,
                                   userChannelsService: UserChannelsService,
                                   tweetsService: TweetsService,
                                   tweetLikesService: TweetLikesService,
                                   followsService: FollowsService,
                                   followersService: FollowersService,
                                   friendsService: FriendsService,
                                   invitationService: InvitationsService,
                                   mutesService: MutesService,
                                   friendRequestsService: FriendRequestsService,
                                   blocksService: BlocksService,
                                   f: CactaceaAuthenticationFilterFactory,
                                   s: Swagger
                                 ) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/session") { o =>
      o.summary("Get user information")
        .tag(sessionTag)
        .operationId("findSession")
        .responseWith[User](Status.Ok.code, successfulMessage)
    } { _: Request =>
      usersService.find(
        CactaceaContext.sessionId
      )
    }

    scope(basic).deleteWithDoc("/session") { o =>
      o.summary("Sign out")
        .tag(sessionTag)
        .operationId("signOut")
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: DeleteSignOut =>
      usersService.signOut(
        request.udid,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).getWithDoc("/session/username/:userName") { o =>
      o.summary("Confirm user name exist")
        .tag(sessionTag)
        .operationId("existUserName")
        .request[GetUserName]
        .responseWith[UserNameNotExists](Status.Ok.code, successfulMessage)
    } { request: GetUserName =>
      usersService.isRegistered(
        request.userName
      ).map(r => response.ok(UserNameNotExists(request.userName, r)))
    }

    scope(basic).putWithDoc("/session/profile") { o =>
      o.summary("Update the profile")
        .tag(sessionTag)
        .operationId("updateProfile")
        .request[PutSessionProfile]
        .responseWith(Status.Ok.code, successfulMessage)
    }  { request: PutSessionProfile =>
      usersService.updateProfile(
        request.displayName,
        request.web,
        request.birthday,
        request.location,
        request.bio,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).putWithDoc("/session/profile_image") { o =>
      o.summary("Update the profile image")
        .tag(sessionTag)
        .operationId("updateProfileImage")
        .request[PutSessionProfileImage]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
    }  { request: PutSessionProfileImage =>
      usersService.updateProfileImage(
        request.id,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).deleteWithDoc("/session/profile_image") { o =>
      o.summary("Remove the profile image")
        .tag(sessionTag)
        .operationId("deleteProfileImage")
        .responseWith(Status.Ok.code, successfulMessage)
    }  { _: Request =>
      usersService.deleteProfileImage(
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).getWithDoc("/session/blocks") { o =>
      o.summary("Get block users list")
        .tag(sessionTag)
        .operationId("findSessionBlocks")
        .request[GetSessionBlocks]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
    } { request: GetSessionBlocks =>
      blocksService.find(
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(tweets).getWithDoc("/session/tweets") { o =>
      o.summary("Find session tweets")
        .tag(sessionTag)
        .operationId("findSessionTweets")
        .request[GetSessionTweets]
        .responseWith[Seq[Tweet]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(UserNotFound))))
    } { request: GetSessionTweets =>
      tweetsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.tweetPrivacyType,
        request.tweetType.getOrElse(TweetType.received),
        CactaceaContext.sessionId
      )
    }

    scope(tweets).getWithDoc("/session/likes") { o =>
      o.summary("Get tweets list session user set a like")
        .tag(sessionTag)
        .operationId("findSessionLikes")
        .request[GetSessionLikedTweets]
        .responseWith[Seq[Tweet]](Status.Ok.code, successfulMessage)
    } { request: GetSessionLikedTweets =>
      tweetLikesService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/session/follows") { o =>
      o.summary("Get users list session user followed")
        .tag(sessionTag)
        .operationId("findSessionFollows")
        .request[GetSessionFollows]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollows =>
      followsService.find(
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/session/followers") { o =>
      o.summary("Get users list session user is followed by")
        .tag(sessionTag)
        .operationId("findSessionFollowers")
        .request[GetSessionFollowers]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollowers =>
      followersService.find(
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(relationships).getWithDoc("/session/friends") { o =>
      o.summary("Get friends list")
        .tag(sessionTag)
        .operationId("findSessionFriends")
        .request[GetSessionFriends]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
    }  { request: GetSessionFriends =>
      friendsService.find(
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(channels).getWithDoc("/session/channels") { o =>
      o.summary("Get channels list session user channelJoined")
        .tag(sessionTag)
        .operationId("findSessionChannels")
        .request[GetSessionChannels]
        .responseWith[Seq[Channel]](Status.Ok.code, successfulMessage)
    } { request: GetSessionChannels =>
      userChannelsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        true,
        CactaceaContext.sessionId
      )
    }

    scope(channels).getWithDoc("/session/hides") { o =>
      o.summary("Get hidden channels list session user channelJoined")
        .tag(sessionTag)
        .operationId("findSessionHiddenChannels")
        .request[GetSessionChannels]
        .responseWith[Seq[Channel]](Status.Ok.code, successfulMessage)

    } { request: GetSessionChannels =>
      userChannelsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        false,
        CactaceaContext.sessionId
      )
    }

    scope(invitations).getWithDoc("/session/invitations") { o =>
      o.summary("Get invitations list session user received")
        .tag(sessionTag)
        .operationId("findSessionInvitations")
        .request[GetSessionInvitations]
        .responseWith[Seq[Invitation]](Status.Ok.code, successfulMessage)
    } { request: GetSessionInvitations =>
      invitationService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/session/mutes") { o =>
      o.summary("Get users list session user muted")
        .tag(sessionTag)
        .operationId("findSessionMutes")
        .request[GetSessionMutes]
        .responseWith[Seq[User]](Status.Ok.code, successfulMessage)
    } { request: GetSessionMutes =>
      mutesService.find(
        request.userName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(relationships).getWithDoc("/session/requests") { o =>
      o.summary("Get friend requests list session user created or received")
        .tag(sessionTag)
        .operationId("findSessionFriendRequests")
        .request[GetSessionFriendRequests]
        .responseWith[Seq[FriendRequest]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFriendRequests =>
      friendRequestsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.received,
        CactaceaContext.sessionId
      )
    }

  }

}
