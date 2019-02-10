package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.GroupPrivacyType
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao.{FollowersDAO, FollowingsDAO, GroupInvitationsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountsValidator, FollowingsValidator}

@Singleton
class FollowingsRepository @Inject()(
                                      accountsValidator: AccountsValidator,
                                      followingsValidator: FollowingsValidator,
                                      followingsDAO: FollowingsDAO,
                                      followersDAO: FollowersDAO,
                                      groupInvitationsDAO: GroupInvitationsDAO
                                ) {

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    for {
      _ <- accountsValidator.exist(accountId, sessionId)
      r <- followingsDAO.find(accountId, since, offset, count, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId) : Future[List[Account]]= {
    followingsDAO.find(since, offset, count, sessionId)
  }

  def create(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- followingsValidator.notExist(accountId, sessionId)
      _ <- followingsDAO.create(accountId, sessionId)
      _ <- followersDAO.create(accountId, sessionId)
    } yield (Unit)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- followingsValidator.exist(accountId, sessionId)
      _ <- followingsDAO.delete(accountId, sessionId)
      _ <- followersDAO.delete(accountId, sessionId)
      _ <- groupInvitationsDAO.delete(accountId, GroupPrivacyType.following, sessionId)
      _ <- groupInvitationsDAO.delete(sessionId.toAccountId, GroupPrivacyType.followers, accountId.toSessionId)
    } yield (Unit)
  }

}
