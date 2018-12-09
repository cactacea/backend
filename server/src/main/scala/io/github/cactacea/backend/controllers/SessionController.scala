package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.enums.FriendsSortType
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.models.requests.feed.{GetSessionFeeds, GetSessionLikedFeeds}
import io.github.cactacea.backend.models.requests.group.{GetSessionGroups, GetSessionInvitations}
import io.github.cactacea.backend.models.requests.session._
import io.github.cactacea.backend.swagger.CactaceaController
import io.github.cactacea.backend.utils.auth.SessionContext
import io.github.cactacea.backend.utils.oauth.Permissions
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
                                   friendRequestsService: FriendRequestsService
                                 ) extends CactaceaController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithPermission("/session")(Permissions.basic) { o =>
      o.summary("Get basic information about session account")
        .tag(sessionTag)
        .operationId("find")
        .responseWith[Account](Status.Ok.code, successfulMessage)
    } { _: Request =>
      accountsService.find(
        SessionContext.id
      )
    }


    deleteWithPermission("/session")(Permissions.basic) { o =>
      o.summary("Sign out")
        .tag(sessionTag)
        .operationId("signOut")
        .responseWith(Status.Ok.code, successfulMessage)
    } { _: Request =>
      sessionService.signOut(
        SessionContext.udid,
        SessionContext.id
      ).map(_ => response.ok)
    }


    putWithPermission("/session/account_name")(Permissions.basic) { o =>
      o.summary("Update the account name")
        .tag(sessionTag)
        .operationId("updateAccountName")
        .request[PutSessionAccountName]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.BadRequest.code, Status.BadRequest.reason, Some(CactaceaErrors(Seq(AccountNameAlreadyUsed))))
    } { request: PutSessionAccountName =>
      accountsService.update(
        request.name,
        SessionContext.id
      ).map(_ => response.ok)
    }


    putWithPermission("/session/password")(Permissions.basic) { o =>
      o.summary("Update the password")
        .tag(sessionTag)
        .operationId("updatePassword")
        .request[PutSessionPassword]
        .responseWith(Status.Ok.code, successfulMessage)
    } { request: PutSessionPassword =>
      accountsService.update(
        request.oldPassword,
        request.newPassword,
        SessionContext.id
      ).map(_ => response.ok)
    }


    putWithPermission("/session/profile")(Permissions.basic) { o =>
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
        SessionContext.id
      ).map(_ => response.ok)
    }

    putWithPermission("/session/profile_image")(Permissions.basic) { o =>
      o.summary("Update the profile image")
        .tag(sessionTag)
        .operationId("updateProfileImage")
        .request[PutSessionProfileImage]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(MediumNotFound))))
    }  { request: PutSessionProfileImage =>
      accountsService.updateProfileImage(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    deleteWithPermission("/session/profile_image")(Permissions.basic) { o =>
      o.summary("Remove the profile image")
        .tag(sessionTag)
        .operationId("deleteProfileImage")
        .responseWith(Status.Ok.code, successfulMessage)
    }  { _: Request =>
      accountsService.deleteProfileImage(
        SessionContext.id
      ).map(_ => response.ok)
    }

    getWithPermission("/session/feeds")(Permissions.basic) { o =>
      o.summary("Get feeds list session account posted")
        .tag(sessionTag)
        .operationId("findFeeds")
        .request[GetSessionFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFeeds =>
      feedsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    getWithPermission("/session/likes")(Permissions.basic) { o =>
      o.summary("Get feeds list session account set a like")
        .tag(sessionTag)
        .operationId("findLikes")
        .request[GetSessionLikedFeeds]
        .responseWith[Array[Feed]](Status.Ok.code, successfulMessage)
    } { request: GetSessionLikedFeeds =>
      feedLikesService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    getWithPermission("/session/follows")(Permissions.followerList) { o =>
      o.summary("Get accounts list session account followed")
        .tag(sessionTag)
        .operationId("findFollows")
        .request[GetSessionFollows]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollows =>
      followsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    getWithPermission("/session/followers")(Permissions.followerList) { o =>
      o.summary("Get accounts list session account is followed by")
        .tag(sessionTag)
        .operationId("findFollowers")
        .request[GetSessionFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollowers =>
      followersService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    getWithPermission("/session/friends")(Permissions.followerList) { o =>
      o.summary("Get friends list")
        .tag(sessionTag)
        .operationId("findFriends")
        .request[GetSessionFriends]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    }  { request: GetSessionFriends =>
      friendsService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.sortType.getOrElse(FriendsSortType.friendsAt),
        SessionContext.id
      )
    }

    getWithPermission("/session/groups")(Permissions.basic) { o =>
      o.summary("Get groups list session account joined")
        .tag(sessionTag)
        .operationId("findGroups")
        .request[GetSessionGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
    } { request: GetSessionGroups =>
      accountGroupsService.findAll(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        true,
        SessionContext.id
      )
    }

    getWithPermission("/session/hides")(Permissions.basic) { o =>
      o.summary("Get hidden groups list session account joined")
        .tag(sessionTag)
        .operationId("findHides")
        .request[GetSessionGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)

    } { request: GetSessionGroups =>
      accountGroupsService.findAll(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        false,
        SessionContext.id
      )
    }

    getWithPermission("/session/invitations")(Permissions.basic) { o =>
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
        SessionContext.id
      )
    }

    getWithPermission("/session/mutes")(Permissions.basic) { o =>
      o.summary("Get accounts list session account muted")
        .tag(sessionTag)
        .operationId("findMutes")
        .request[GetSessionMutes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionMutes =>
      mutesService.find(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        SessionContext.id
      )
    }

    getWithPermission("/session/requests")(Permissions.basic) { o =>
      o.summary("Get friend requests list session account created or received")
        .tag(sessionTag)
        .operationId("findFriendRequests")
        .request[GetSessionFriendRequests]
        .responseWith[Array[FriendRequest]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFriendRequests =>
      friendRequestsService.findAll(
        request.since,
        request.offset.getOrElse(0),
        request.count.getOrElse(20),
        request.received,
        SessionContext.id
      )
    }

  }

}
