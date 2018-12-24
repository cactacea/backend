package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{FriendsSortType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FriendsRepository @Inject()(
                                   accountsDAO: AccountsDAO,
                                   followingsDAO: FollowingsDAO,
                                   followersDAO: FollowersDAO,
                                   friendsDAO: FriendsDAO,
                                   groupInvitationsDAO: GroupInvitationsDAO
                                 ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendsDAO.validateNotExist(accountId, sessionId)
      _ <- friendsDAO.create(accountId, sessionId)
      _ <- followingsDAO.create(accountId, sessionId)
      _ <- followingsDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.create(accountId, sessionId)
      _ <- followersDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendsDAO.create(sessionId.toAccountId, accountId.toSessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- friendsDAO.validateExist(accountId, sessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.friends, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.friends, accountId.toSessionId)
    } yield (Unit)
  }

  def find(since: Option[Long], offset: Int, count: Int, sortType: FriendsSortType, sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.find(since, offset, count, sortType, sessionId)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.find(
      accountId,
      since,
      offset,
      count,
      sessionId)
  }


}
