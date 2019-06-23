package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.enums.FriendsSortType
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.account.GetAccountName
import io.github.cactacea.backend.models.requests.feed.{GetSessionFeeds, GetSessionLikedFeeds}
import io.github.cactacea.backend.models.requests.group.{GetSessionGroups, GetSessionInvitations}
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.backend.models.responses.AccountNameNotExists
import io.github.cactacea.backend.swagger.CactaceaSwaggerController
import io.github.cactacea.backend.utils.auth.CactaceaContext

import io.swagger.models.Swagger


@Singleton
class SessionController @Inject()(
                                   @Flag("cactacea.api.prefix") apiPrefix: String,
                                   s: Swagger,
                                   accountsService: AccountsService,
                                   accountGroupsService: AccountGroupsService,
                                   feedsService: FeedsService,
                                   feedLikesService: FeedLikesService,
                                   followsService: FollowsService,
                                   followersService: FollowersService,
                                   friendsService: FriendsService,
                                   invitationService: GroupInvitationsService,
                                   mutesService: MutesService,
                                   sessionService: SessionsService,
                                   friendRequestsService: FriendRequestsService,
                                   blocksService: BlocksService
                                 ) extends CactaceaSwaggerController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/session") { o =>
      o.summary("Get basic information about session account")
        .tag(sessionTag)
        .operationId("findSession")
        .responseWith[Account](Status.Ok.code, successfulMessage)
    } { _: Request =>
      accountsService.find(
        CactaceaContext.id
      )
    }


    deleteWithDoc("/session") { o =>
      o.summary("Sign out")
        .tag(sessionTag)
        .operationId("signOut")
        .responseWith(Status.Ok.code, successfulMessage)
    } { _: Request =>
      sessionService.signOut(
        CactaceaContext.udid,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    getWithDoc("/session/account_name/:accountName") { o =>
      o.summary("Confirm account name exist")
        .tag(sessionTag)
        .operationId("existAccountName")
        .request[GetAccountName]
        .responseWith[AccountNameNotExists](Status.Ok.code, successfulMessage)
    } { request: GetAccountName =>
      accountsService.notExist(
        request.accountName
      ).map(r => response.ok(AccountNameNotExists(request.accountName, r)))
    }

    putWithDoc("/session/account_name") { o =>
      o.summary("Update the account name")
        .tag(sessionTag)
        .operationId("updateAccountName")
        .request[PutSessionAccountName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNameAlreadyUsed))))
    } { request: PutSessionAccountName =>
      accountsService.update(
        request.name,
        CactaceaContext.id
      ).map(_ => response.ok)
    }


    putWithDoc("/session/password") { o =>
      o.summary("Update the password")
        .tag(sessionTag)
        .operationId("updatePassword")
        .request[PutSessionPassword]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutSessionPassword =>
      accountsService.update(
        request.oldPassword,
        request.newPassword,
        CactaceaContext.id
      ).map(_ => response.ok)
    }


    putWithDoc("/session/profile") { o =>
      o.summary("Update the profile")
        .tag(sessionTag)
        .operationId("updateProfile")
        .request[PutSessionProfile]
        .responseWith(Status.Ok.code, successfulMessage)
    }  { request: PutSessionProfile =>
      accountsService.update(
        request.displayName,
        request.web,
        request.birthday,
        request.location,
        request.bio,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    putWithDoc("/session/profile_image") { o =>
      o.summary("Update the profile image")
        .tag(sessionTag)
        .operationId("updateProfileImage")
        .request[PutSessionProfileImage]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
    }  { request: PutSessionProfileImage =>
      accountsService.updateProfileImage(
        request.id,
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    deleteWithDoc("/session/profile_image") { o =>
      o.summary("Remove the profile image")
        .tag(sessionTag)
        .operationId("deleteProfileImage")
        .responseWith(Status.Ok.code, successfulMessage)
    }  { _: Request =>
      accountsService.deleteProfileImage(
        CactaceaContext.id
      ).map(_ => response.ok)
    }

    getWithDoc("/session/blocks") { o =>
      o.summary("Get blocking accounts list")
        .tag(blocksTag)
        .operationId("findBlockingAccounts")
        .request[GetSessionBlocks]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionBlocks =>
      blocksService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/feeds") { o =>
      o.summary("Get feeds list session account posted")
        .tag(sessionTag)
        .operationId("findSessionFeeds")
        .request[GetSessionFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFeeds =>
      feedsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/likes") { o =>
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
        CactaceaContext.id
      )
    }

    getWithDoc("/session/follows") { o =>
      o.summary("Get accounts list session account followed")
        .tag(sessionTag)
        .operationId("findSessionFollow")
        .request[GetSessionFollows]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollows =>
      followsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/followers") { o =>
      o.summary("Get accounts list session account is followed by")
        .tag(sessionTag)
        .operationId("findSessionFollowers")
        .request[GetSessionFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollowers =>
      followersService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/friends") { o =>
      o.summary("Get friends list")
        .tag(sessionTag)
        .operationId("findSessionFriends")
        .request[GetSessionFriends]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    }  { request: GetSessionFriends =>
      friendsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.sortType.getOrElse(FriendsSortType.friendsAt),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/groups") { o =>
      o.summary("Get groups list session account groupJoined")
        .tag(sessionTag)
        .operationId("findSessionGroups")
        .request[GetSessionGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
    } { request: GetSessionGroups =>
      accountGroupsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        true,
        CactaceaContext.id
      )
    }

    getWithDoc("/session/hides") { o =>
      o.summary("Get hidden groups list session account groupJoined")
        .tag(sessionTag)
        .operationId("findHiddenGroups")
        .request[GetSessionGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)

    } { request: GetSessionGroups =>
      accountGroupsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        false,
        CactaceaContext.id
      )
    }

    getWithDoc("/session/invitations") { o =>
      o.summary("Get invitations list session account received")
        .tag(sessionTag)
        .operationId("findGroupInvitations")
        .request[GetSessionInvitations]
        .responseWith[Array[GroupInvitation]](Status.Ok.code, successfulMessage)
    } { request: GetSessionInvitations =>
      invitationService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/mutes") { o =>
      o.summary("Get accounts list session account muted")
        .tag(sessionTag)
        .operationId("findMutingAccounts")
        .request[GetSessionMutes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionMutes =>
      mutesService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        CactaceaContext.id
      )
    }

    getWithDoc("/session/requests") { o =>
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
        CactaceaContext.id
      )
    }

  }

}
