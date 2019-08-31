package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.auth.application.services.AuthenticationService
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.server.models.requests.account.GetAccountName
import io.github.cactacea.backend.server.models.requests.feed.{GetSessionFeeds, GetSessionLikedFeeds}
import io.github.cactacea.backend.server.models.requests.channel.{GetSessionChannels, GetSessionInvitations}
import io.github.cactacea.backend.server.models.requests.session._
import io.github.cactacea.backend.server.models.requests.sessions.DeleteSignOut
import io.github.cactacea.backend.server.models.responses.AccountNameNotExists
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization._
import io.github.cactacea.backend.server.utils.context.CactaceaContext
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.github.cactacea.filhouette.impl.providers.CredentialsProvider
import io.swagger.models.Swagger


@Singleton
class SessionController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   s: Swagger,
                                   accountAuthenticationService: AuthenticationService,
                                   accountsService: AccountsService,
                                   accountChannelsService: AccountChannelsService,
                                   feedsService: FeedsService,
                                   feedLikesService: FeedLikesService,
                                   followsService: FollowsService,
                                   followersService: FollowersService,
                                   friendsService: FriendsService,
                                   invitationService: InvitationsService,
                                   mutesService: MutesService,
                                   friendRequestsService: FriendRequestsService,
                                   blocksService: BlocksService
                                 ) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    scope(basic).getWithDoc("/session") { o =>
      o.summary("Get basic information about session account")
        .tag(sessionTag)
        .operationId("findSession")
        .responseWith[Account](Status.Ok.code, successfulMessage)
    } { _: Request =>
      accountsService.find(
        CactaceaContext.sessionId
      )
    }


    scope(basic).deleteWithDoc("/session") { o =>
      o.summary("Sign out")
        .tag(sessionTag)
        .operationId("signOut")
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: DeleteSignOut =>
      accountsService.signOut(
        request.udid,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).getWithDoc("/session/account_name/:accountName") { o =>
      o.summary("Confirm account name exist")
        .tag(sessionTag)
        .operationId("existAccountName")
        .request[GetAccountName]
        .responseWith[AccountNameNotExists](Status.Ok.code, successfulMessage)
    } { request: GetAccountName =>
      accountsService.isRegistered(
        request.accountName
      ).map(r => response.ok(AccountNameNotExists(request.accountName, r)))
    }

    scope(basic).putWithDoc("/session/account_name") { o =>
      o.summary("Update the account name")
        .tag(sessionTag)
        .operationId("updateAccountName")
        .request[PutSessionAccountName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountAlreadyExist))))
    } { request: PutSessionAccountName =>
      accountsService.changeAccountName(
        CredentialsProvider.ID,
        request.name,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }


    scope(basic).putWithDoc("/session/password") { o =>
      o.summary("Update the password")
        .tag(sessionTag)
        .operationId("updatePassword")
        .request[PutSessionPassword]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutSessionPassword =>
      accountAuthenticationService.changePassword(
        request.password,
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }


    scope(basic).putWithDoc("/session/profile") { o =>
      o.summary("Update the profile")
        .tag(sessionTag)
        .operationId("updateProfile")
        .request[PutSessionProfile]
        .responseWith(Status.Ok.code, successfulMessage)
    }  { request: PutSessionProfile =>
      accountsService.updateProfile(
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
      accountsService.updateProfileImage(
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
      accountsService.deleteProfileImage(
        CactaceaContext.sessionId
      ).map(_ => response.ok)
    }

    scope(basic).getWithDoc("/session/blocks") { o =>
      o.summary("Get blocking accounts list")
        .tag(blocksTag)
        .operationId("findBlockingAccounts")
        .request[GetSessionBlocks]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionBlocks =>
      blocksService.find(
        request.accountName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(feeds).getWithDoc("/session/feeds") { o =>
      o.summary("Find session feeds")
        .tag(feedsTag)
        .operationId("findSessionFeeds")
        .request[GetSessionFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(AccountNotFound))))
    } { request: GetSessionFeeds =>
      feedsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.feedPrivacyType,
        CactaceaContext.sessionId
      )
    }

    scope(feeds).getWithDoc("/session/likes") { o =>
      o.summary("Get feeds list session account set a like")
        .tag(sessionTag)
        .operationId("findSessionFeedsLiked")
        .request[GetSessionLikedFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
    } { request: GetSessionLikedFeeds =>
      feedLikesService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/session/follows") { o =>
      o.summary("Get accounts list session account followed")
        .tag(sessionTag)
        .operationId("findSessionFollow")
        .request[GetSessionFollows]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollows =>
      followsService.find(
        request.accountName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/session/followers") { o =>
      o.summary("Get accounts list session account is followed by")
        .tag(sessionTag)
        .operationId("findSessionFollowers")
        .request[GetSessionFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollowers =>
      followersService.find(
        request.accountName,
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
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    }  { request: GetSessionFriends =>
      friendsService.find(
        request.accountName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(channels).getWithDoc("/session/channels") { o =>
      o.summary("Get channels list session account channelJoined")
        .tag(sessionTag)
        .operationId("findSessionChannels")
        .request[GetSessionChannels]
        .responseWith[Array[Channel]](Status.Ok.code, successfulMessage)
    } { request: GetSessionChannels =>
      accountChannelsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        true,
        CactaceaContext.sessionId
      )
    }

    scope(channels).getWithDoc("/session/hides") { o =>
      o.summary("Get hidden channels list session account channelJoined")
        .tag(sessionTag)
        .operationId("findHiddenChannels")
        .request[GetSessionChannels]
        .responseWith[Array[Channel]](Status.Ok.code, successfulMessage)

    } { request: GetSessionChannels =>
      accountChannelsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        false,
        CactaceaContext.sessionId
      )
    }

    scope(invitations).getWithDoc("/session/invitations") { o =>
      o.summary("Get invitations list session account received")
        .tag(sessionTag)
        .operationId("findInvitations")
        .request[GetSessionInvitations]
        .responseWith[Array[Invitation]](Status.Ok.code, successfulMessage)
    } { request: GetSessionInvitations =>
      invitationService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(basic).getWithDoc("/session/mutes") { o =>
      o.summary("Get accounts list session account muted")
        .tag(sessionTag)
        .operationId("findMutingAccounts")
        .request[GetSessionMutes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionMutes =>
      mutesService.find(
        request.accountName,
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.sessionId
      )
    }

    scope(relationships).getWithDoc("/session/requests") { o =>
      o.summary("Get friend requests list session account created or received")
        .tag(sessionTag)
        .operationId("findFriendRequests")
        .request[GetSessionFriendRequests]
        .responseWith[Array[FriendRequest]](Status.Ok.code, successfulMessage)
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
