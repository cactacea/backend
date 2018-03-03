package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.GroupPrivacyType
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FriendsRepository {

  @Inject var friendsDAO: FriendsDAO = _
  @Inject var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject var followsDAO: FollowsDAO = _
  @Inject var followersDAO: FollowersDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.notExistFriends(accountId, sessionId)
      _ <- friendsDAO.create(accountId, sessionId)
      _ <- followsDAO.create(accountId, sessionId)
      _ <- followersDAO.create(accountId, sessionId)
      _ <- friendsDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followsDAO.create(sessionId.toAccountId, accountId.toSessionId)
      _ <- followersDAO.create(sessionId.toAccountId, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.existFriends(accountId, sessionId)
      _ <- friendsDAO.delete(accountId, sessionId)
      _ <- friendsDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.friends, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.friends, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    val accountId = sessionId.toAccountId
    friendsDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map(t => Account(t._1, t._2)))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    friendsDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map(t => Account(t._1, t._2)))
  }


}
