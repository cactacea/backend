package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.GroupPrivacyType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.{FollowersDAO, FollowingsDAO, GroupInvitationsDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}

@Singleton
class FollowsRepository @Inject()(
                                   followsDAO: FollowingsDAO,
                                   followersDAO: FollowersDAO,
                                   groupInvitationsDAO: GroupInvitationsDAO,
                                   validationDAO: ValidationDAO
                                ) {

  def findAll(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    for {
      _ <- validationDAO.existAccount(accountId)
      r <- followsDAO.findAll(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followsDAO.findAll(since, offset, count, sessionId)
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- validationDAO.notExistFollow(accountId, sessionId)
      _ <- followsDAO.create(accountId, sessionId)
      _ <- followersDAO.create(sessionId.toAccountId, accountId.toSessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.notSessionId(accountId, sessionId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- validationDAO.existFollow(accountId, sessionId)
      _ <- followsDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(sessionId.toAccountId, accountId.toSessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.follows, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.followers, accountId.toSessionId)
    } yield (Unit)
  }

}
