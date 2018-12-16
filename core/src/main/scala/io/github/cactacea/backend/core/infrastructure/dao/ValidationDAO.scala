package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class ValidationDAO @Inject()(
                               accountsDAO: AccountsDAO,
                               accountGroupsDAO: AccountGroupsDAO,
                               blocksDAO: BlocksDAO,
                               commentsDAO: CommentsDAO,
                               commentLikesDAO: CommentLikesDAO,
                               followingsDAO: FollowingsDAO,
                               friendsDAO: FriendsDAO,
                               friendRequestsDAO: FriendRequestsDAO,
                               feedsDAO: FeedsDAO,
                               feedLikesDAO: FeedLikesDAO,
                               groupsDAO: GroupsDAO,
                               groupAccountsDAO: GroupAccountsDAO,
                               groupInvitationsDAO: GroupInvitationsDAO,
                               groupAuthorityDAO: GroupAuthorityDAO,
                               mediumsDAO: MediumsDAO,
                               mutesDAO: MutesDAO
                             ) {

  def notSessionId(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    if (accountId == by) {
      Future.exception(CactaceaException(CanNotSpecifyMyself))
    } else {
      Future.Unit
    }
  }

  def notExistAccountName(accountName: String): Future[Unit] = {
    accountsDAO.exist(accountName).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountNameAlreadyUsed))
    })
  }

  def existComment(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentsDAO.exist(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def notExistCommentLike(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentLikesDAO.exist(commentId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(CommentAlreadyLiked))
    })
  }

  def existCommentLike(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentLikesDAO.exist(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotLiked))
    })
  }

  def existFeed(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedsDAO.exist(feedId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def existAccount(accountId: AccountId): Future[Unit] = {
    accountsDAO.exist(accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def existAccount(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    accountsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def existBlock(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotBlocked))
    })
  }

  def notExistBlock(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyBlocked))
      case false =>
        Future.Unit
    })
  }

  def existFollow(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    followingsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFollowed))
    })
  }

  def notExistFollow(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    followingsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFollowed))
      case false =>
        Future.Unit
    })
  }

  def findAccount(accountId: AccountId): Future[Accounts] = {
    accountsDAO.find(accountId.toSessionId).flatMap( _ match {
      case Some(a) =>
        Future.value(a)
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def findMedium(mediumId: MediumId, sessionId: SessionId): Future[Mediums] = {
    mediumsDAO.find(mediumId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case None =>
        Future.exception(CactaceaException(MediumNotFound))
    })
  }

  def existMediums(mediumIdsOpt: Option[List[MediumId]], sessionId: SessionId): Future[Unit] = {
    mediumIdsOpt match {
      case Some(mediumIds) =>
        mediumsDAO.exist(mediumIds, sessionId).flatMap(_ match {
          case true =>
            Future.Unit
          case false =>
            Future.exception(CactaceaException(MediumNotFound))
        })
      case None =>
        Future.Unit
    }
  }

  def existMediums(mediumId: MediumId, sessionId: SessionId): Future[Unit] = {
    mediumsDAO.exist(mediumId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(MediumNotFound))
    })
  }

  def notExistFeedLike(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedLikesDAO.exist(feedId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(FeedAlreadyLiked))
    })
  }

  def existFeedLike(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedLikesDAO.exist(feedId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FeedNotLiked))
      case true =>
        Future.Unit
    })
  }

  def existFriendRequest(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FriendRequestNotFound))
      case true =>
        Future.Unit
    })
  }

  def notExistFriendRequest(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyRequested))
      case false =>
        Future.Unit
    })
  }

  def findFriendRequest(friendRequestId: FriendRequestId, sessionId: SessionId): Future[FriendRequests] = {
    friendRequestsDAO.find(friendRequestId, sessionId).flatMap(_ match {
      case Some(r) =>
        Future.value(r)
      case None =>
        Future.exception(CactaceaException(FriendRequestNotFound))
    })
  }

  def notExistFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFriend))
      case false =>
        Future.Unit
    })
  }

  def existFriend(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(AccountNotFriend))
      case true =>
        Future.Unit
    })
  }

  def findGroup(groupId: GroupId): Future[Groups] = {
    groupsDAO.find(groupId).flatMap(_ match {
      case Some(g) =>
        Future.value(g)
      case None =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def findAccountGroup(groupId: GroupId, sessionId: SessionId): Future[(AccountGroups, Groups)] = {
    accountGroupsDAO.findByGroupId(groupId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(t)
      case _ =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def findNotInvitationOnlyGroup(groupId: GroupId): Future[Groups] = {
    groupsDAO.find(groupId).flatMap(_ match {
      case Some(g) =>
        if (g.invitationOnly == true) {
          Future.exception(CactaceaException(GroupIsInvitationOnly))
        } else {
          Future.value(g)
        }
      case None =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def existGroup(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    groupsDAO.exist(groupId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }


  def existGroupAccount(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupAccountsDAO.exist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def notExistGroupAccount(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupAccountsDAO.exist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

  def notExistGroupInvitation(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupInvitationsDAO.exist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def notExistMute(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyMuted))
      case false =>
        Future.Unit
    })
  }

  def existMute(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotMuted))
    })
  }

  def hasJoinAuthority(g: Groups, sessionId: SessionId): Future[Unit] = {
    groupAuthorityDAO.hasJoinAuthority(g, sessionId).flatMap(_ match {
      case Right(_) =>
        Future.Unit
      case Left(e) =>
        Future.exception(CactaceaException(e))
    })
  }

  def hasJoinAndManagingAuthority(g: Groups, accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    groupAuthorityDAO.hasJoinAndManagingAuthority(g, accountId, sessionId).flatMap(_ match {
      case Right(_) =>
        Future.Unit
      case Left(e) =>
        Future.exception(CactaceaException(e))
    })
  }

  def findGroupsInvitation(groupInvitationId: GroupInvitationId, sessionId: SessionId): Future[GroupInvitations] = {
    groupInvitationsDAO.find(groupInvitationId, sessionId).flatMap(_ match {
      case None =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
      case Some(i) =>
        Future.value(i)
    })
  }

  def checkGroupAccountsCount(groupId: GroupId): Future[Unit] = {
    Future.Unit
//    if (configService.maxGroupAccountsCount > 0) {
//      groupAccountsDAO.findCount(groupId).map({ count =>
//        if (count >= configService.maxGroupAccountsCount) {
//          Future.exception(CactaceaException(CactaceaError.GroupAccountsCountLimitError))
//        } else {
//          Future.Unit
//        }
//      })
//    } else {
//      Future.Unit
//    }
  }

}
