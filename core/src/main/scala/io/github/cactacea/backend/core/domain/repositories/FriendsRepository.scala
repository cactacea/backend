package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.GroupPrivacyType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FriendsRepository {

  @Inject private var friendsDAO: FriendsDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var followingDAO: FollowingDAO = _
  @Inject private var followersDAO: FollowersDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.notExistFriend(accountId, sessionId)
      _ <- friendsDAO.create(accountId, sessionId)
      _ <- followingDAO.create(accountId, sessionId)
      _ <- followersDAO.create(accountId, sessionId)
      _ <- friendsDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followingDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.create(sessionId.toAccountId, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existFriend(accountId, sessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.friends, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.friends, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.findAll(since, offset, count, sessionId)
      .map(_.map({ case (a, r, n) => Account(a, r, n.friendedAt)}))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map({ case (a, r, n) => Account(a, r, n.friendedAt)}))
  }


}
