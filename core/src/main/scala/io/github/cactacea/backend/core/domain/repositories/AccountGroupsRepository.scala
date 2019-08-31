package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, GroupsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountsValidator}


class AccountGroupsRepository @Inject()(
                                         accountsValidator: AccountsValidator,
                                         accountGroupsValidator: AccountGroupsValidator,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         groupsDAO: GroupsDAO
                                       ) {

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountGroupsValidator.mustJoined(sessionId.toAccountId, groupId)
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
      _ <- accountMessagesDAO.delete(sessionId.toAccountId, groupId)
    } yield (())
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Group]] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      r <- accountGroupsDAO.find(accountId, since, offset, count, false, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsDAO.find(sessionId.toAccountId, since, offset, count, hidden, sessionId)
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      g <- accountGroupsDAO.findByAccountId(accountId, sessionId)
      r <- createIfNeeded(g, accountId, sessionId)
    } yield (r)
  }

  private def createIfNeeded(group: Option[Group], accountId: AccountId, sessionId: SessionId): Future[Group] = {
    group match {
      case None =>
        for {
          i <- groupsDAO.create(sessionId)
          _ <- accountGroupsDAO.create(i, sessionId)
          _ <- accountGroupsDAO.create(accountId, i, sessionId)
          g <- accountGroupsValidator.mustFindByAccountId(accountId, sessionId)
        } yield (g)
      case Some(g) =>
        Future.value(g)
    }
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountGroupsValidator.mustHidden(groupId, sessionId)
      _ <- accountGroupsDAO.updateHidden(groupId, false, sessionId)
    } yield (())
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountGroupsValidator.mustNotHidden(groupId, sessionId)
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
    } yield (())
  }

}
