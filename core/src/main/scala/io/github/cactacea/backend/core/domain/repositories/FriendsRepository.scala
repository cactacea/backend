package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{FriendsSortType, GroupPrivacyType}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FriendsRepository @Inject()(
                                   friendsDAO: FriendsDAO,
                                   groupInvitationsDAO: GroupInvitationsDAO,
                                   followsDAO: FollowsDAO,
                                   followersDAO: FollowersDAO,
                                   validationDAO: ValidationDAO
                                 ) {

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- validationDAO.notExistFriend(accountId, sessionId)
      _ <- friendsDAO.create(accountId, sessionId)
      _ <- followsDAO.create(accountId, sessionId)
      _ <- followersDAO.create(accountId, sessionId)
      _ <- friendsDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followsDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.create(sessionId.toAccountId, accountId.toSessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- validationDAO.existFriend(accountId, sessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.friends, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.friends, accountId.toSessionId)
    } yield (Unit)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, sortType: FriendsSortType, sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.findAll(since, offset, count, sortType, sessionId)
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.findAll(
      accountId,
      since,
      offset,
      count,
      sessionId)
  }


}
