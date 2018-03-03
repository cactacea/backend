package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.GroupPrivacyType
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao.{FollowersDAO, FollowsDAO, GroupInvitationsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowsRepository {

  @Inject var followsDAO: FollowsDAO = _
  @Inject var followersDAO: FollowersDAO = _
  @Inject var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    followsDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map(t => Account(t._1, t._2)))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId) : Future[List[Account]]= {
    val accountId = sessionId.toAccountId
    followsDAO.findAll(accountId, since, offset, count, sessionId)
      .map(_.map(t => Account(t._1, t._2)))
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.notExistFollows(accountId, sessionId)
      _ <- followsDAO.create(accountId, sessionId)
      _ <- followersDAO.create(accountId, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.existFollows(accountId, sessionId)
      _ <- followsDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(accountId, sessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.follows, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.followers, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

}
