package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models.{FriendRequests, GroupInvitations, Groups}
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException

@Singleton
class ValidationDAO {

  @Inject var accountsDAO: AccountsDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var blocksDAO: BlocksDAO = _
  @Inject var commentsDAO: CommentsDAO = _
  @Inject var commentFavoritesDAO: CommentFavoritesDAO = _
  @Inject var followsDAO: FollowsDAO = _
  @Inject var followersDAO: FollowersDAO = _
  @Inject var friendsDAO: FriendsDAO = _
  @Inject var friendRequestsDAO: FriendRequestsDAO = _
  @Inject var feedsDAO: FeedsDAO = _
  @Inject var feedFavoritesDAO: FeedFavoritesDAO = _
  @Inject var groupsDAO: GroupsDAO = _
  @Inject var groupAccountsDAO: GroupAccountsDAO = _
  @Inject var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject var groupAuthorityDAO: GroupAuthorityDAO = _
  @Inject var mediumsDAO: MediumsDAO = _
  @Inject var mutesDAO: MutesDAO = _

  def notSessionId(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    if (accountId == by) {
      Future.exception(CactaceaException(CanNotSpecifyMyself))
    } else {
      Future.Unit
    }
  }

  def notExistAccountName(displayName: String): Future[Unit] = {
    accountsDAO.exist(displayName).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(AccountNameAlreadyUsed))
    })
  }

  def existComments(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentsDAO.exist(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotFound))
    })
  }

  def notExistCommentFavorites(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentFavoritesDAO.exist(commentId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(CommentAlreadyFavorited))
    })
  }

  def existCommentFavorites(commentId: CommentId, sessionId: SessionId): Future[Unit] = {
    commentFavoritesDAO.exist(commentId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(CommentNotFavorited))
    })
  }

  def existFeeds(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedsDAO.exist(feedId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(FeedNotFound))
    })
  }

  def existAccounts(accountId: AccountId): Future[Unit] = {
    accountsDAO.exist(accountId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def existAccounts(accountId: AccountId, sessionId: SessionId, ignoreBlockedUser: Boolean = true): Future[Unit] = {
    accountsDAO.exist(accountId, sessionId, ignoreBlockedUser).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  def existBlocks(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotBlocked))
    })
  }

  def notExistBlocks(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    blocksDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyBlocked))
      case false =>
        Future.Unit
    })
  }

  def existFollows(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    followsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotFollowed))
    })
  }

  def notExistFollows(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    followsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFollowed))
      case false =>
        Future.Unit
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

  def notExistFeedFavorites(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedFavoritesDAO.exist(feedId, sessionId).flatMap(_ match {
      case false =>
        Future.Unit
      case true =>
        Future.exception(CactaceaException(FeedAlreadyFavorited))
    })
  }

  def existFeedFavorites(feedId: FeedId, sessionId: SessionId): Future[Unit] = {
    feedFavoritesDAO.exist(feedId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FeedNotFavorited))
      case true =>
        Future.Unit
    })
  }

  def existFriendRequests(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FriendRequestNotFound))
      case true =>
        Future.Unit
    })
  }

  def notExistFriendRequests(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendRequestsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyRequested))
      case false =>
        Future.Unit
    })
  }

  def findFriendRequests(friendRequestId: FriendRequestId, sessionId: SessionId): Future[FriendRequests] = {
    friendRequestsDAO.find(friendRequestId, sessionId).flatMap(_ match {
      case Some(r) =>
        Future.value(r)
      case None =>
        Future.exception(CactaceaException(FriendRequestNotFound))
    })
  }

  def notExistFriends(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyFriend))
      case false =>
        Future.Unit
    })
  }

  def existFriends(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    friendsDAO.exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(AccountNotFriend))
      case true =>
        Future.Unit
    })
  }

  def findGroups(groupId: GroupId): Future[Groups] = {
    groupsDAO.find(groupId).flatMap(_ match {
      case Some(g) =>
        Future.value(g)
      case None =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def findNotInvitationOnlyGroups(groupId: GroupId): Future[Groups] = {
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

  def existGroups(groupId: GroupId): Future[Unit] = {
    groupsDAO.exist(groupId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }


  def existGroupAccounts(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupAccountsDAO.exist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def notExistGroupAccounts(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupAccountsDAO.exist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyJoined))
      case false =>
        Future.Unit
    })
  }

  def notExistGroupInvites(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupInvitationsDAO.exist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def notExistMutes(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    mutesDAO.exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyMuted))
      case false =>
        Future.Unit
    })
  }

  def existMutes(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
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

  def findGroupsInvite(groupInvitationId: GroupInvitationId, sessionId: SessionId): Future[GroupInvitations] = {
    groupInvitationsDAO.find(groupInvitationId, sessionId).flatMap(_ match {
      case None =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
      case Some(i) =>
        Future.value(i)
    })
  }

}
