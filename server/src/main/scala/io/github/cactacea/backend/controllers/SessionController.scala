package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.core.application.services._
import io.github.cactacea.backend.core.domain.models._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors
import io.github.cactacea.backend.models.requests.account.{PostAcceptFriendRequest, PostRejectFriendRequest}
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
        .operationId("findSession")
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
        .operationId("updateSessionAccountName")
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
        .operationId("updateSessionPassword")
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
        .operationId("updateSessionProfile")
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
        .operationId("updateSessionProfileImage")
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
        .operationId("deleteSessionProfileImage")
        .responseWith(Status.Ok.code, successfulMessage)
    }  { _: Request =>
      accountsService.deleteProfileImage(
        SessionContext.id
      ).map(_ => response.ok)
    }

    getWithPermission("/session/feeds")(Permissions.basic) { o =>
      o.summary("Get feeds list session account posted")
        .tag(sessionTag)
        .operationId("findSessionFeeds")
        .request[GetSessionFeeds]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
    } { request: GetSessionFeeds =>
      feedsService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/likes")(Permissions.basic) { o =>
      o.summary("Get feeds list session account set a like")
        .tag(sessionTag)
        .operationId("findSessionLikes")
        .request[GetSessionLikedFeeds]
        .responseWith[Feed](Status.Ok.code, successfulMessage)
    } { request: GetSessionLikedFeeds =>
      feedLikesService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/follows")(Permissions.followerList) { o =>
      o.summary("Get accounts list session account followed")
        .tag(sessionTag)
        .operationId("findSessionFollows")
        .request[GetSessionFollows]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollows =>
      followsService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/followers")(Permissions.followerList) { o =>
      o.summary("Get accounts list session account is followed by")
        .tag(sessionTag)
        .operationId("findSessionFollowers")
        .request[GetSessionFollowers]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFollowers =>
      followersService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/friends")(Permissions.followerList) { o =>
      o.summary("Get friends list")
        .tag(sessionTag)
        .operationId("findSessionFriends")
        .request[GetSessionFriends]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    }  { request: GetSessionFriends =>
      friendsService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/groups")(Permissions.basic) { o =>
      o.summary("Get groups list session account joined")
        .tag(groupsTag)
        .operationId("findSessionGroups")
        .request[GetSessionGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)
    } { request: GetSessionGroups =>
      accountGroupsService.findAll(
        request.since,
        request.offset,
        request.count,
        true,
        SessionContext.id
      )
    }

    getWithPermission("/session/hides")(Permissions.basic) { o =>
      o.summary("Get hidden groups list session account joined")
        .tag(groupsTag)
        .operationId("findSessionHides")
        .request[GetSessionGroups]
        .responseWith[Array[Group]](Status.Ok.code, successfulMessage)

    } { request: GetSessionGroups =>
      accountGroupsService.findAll(
        request.since,
        request.offset,
        request.count,
        false,
        SessionContext.id
      )
    }

    getWithPermission("/session/invitations")(Permissions.basic) { o =>
      o.summary("Get invitations list session account received")
        .tag(invitationsTag)
        .operationId("findSessionGroupInvitations")
        .request[GetSessionInvitations]
        .responseWith[Array[GroupInvitation]](Status.Ok.code, successfulMessage)
    } { request: GetSessionInvitations =>
      invitationService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/mutes")(Permissions.basic) { o =>
      o.summary("Get accounts list session account muted")
        .tag(mutesTag)
        .operationId("findSessionMutes")
        .request[GetSessionMutes]
        .responseWith[Array[Account]](Status.Ok.code, successfulMessage)
    } { request: GetSessionMutes =>
      mutesService.find(
        request.since,
        request.offset,
        request.count,
        SessionContext.id
      )
    }

    getWithPermission("/session/requests")(Permissions.basic) { o =>
      o.summary("Get friend requests list session account created or received")
        .tag(RequestsTag)
        .operationId("findSessionFriendRequests")
        .request[GetSessionFriendRequests]
        .responseWith[Array[FriendRequest]](Status.Ok.code, successfulMessage)
    } { request: GetSessionFriendRequests =>
      friendRequestsService.findAll(
        request.since,
        request.offset,
        request.count,
        request.received,
        SessionContext.id
      )
    }

    postWithPermission("/session/requests/:id/accept")(Permissions.friendRequests) { o =>
      o.summary("Accept a friend request")
        .tag(RequestsTag)
        .operationId("accept")
        .request[PostAcceptFriendRequest]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FriendRequestNotFound))))
    } { request: PostAcceptFriendRequest =>
      friendRequestsService.accept(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

    postWithPermission("/session/requests/:id/reject")(Permissions.friendRequests) { o =>
      o.summary("Reject a friend request")
        .tag(RequestsTag)
        .operationId("reject")
        .request[PostRejectFriendRequest]
        .responseWith(Status.Ok.code, successfulMessage)
        .responseWith[CactaceaErrors](Status.NotFound.code, Status.NotFound.reason, Some(CactaceaErrors(Seq(FriendRequestNotFound))))
    } { request: PostRejectFriendRequest =>
      friendRequestsService.reject(
        request.id,
        SessionContext.id
      ).map(_ => response.ok)
    }

  }

}
