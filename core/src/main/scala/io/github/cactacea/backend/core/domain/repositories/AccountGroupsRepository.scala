package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, AccountsDAO, GroupsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotJoined, GroupAlreadyHidden, GroupNotHidden}

@Singleton
class AccountGroupsRepository @Inject()(
                                         accountsDAO: AccountsDAO,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         groupsDAO: GroupsDAO
                                       ) {

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
    } yield (Unit)
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Group]] = {
    for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      r <- accountGroupsDAO.find(accountId, since, offset, count, false)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    val accountId = sessionId.toAccountId
    accountGroupsDAO.find(accountId, since, offset, count, hidden)
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    (for {
      _ <- accountsDAO.validateSessionId(accountId, sessionId)
      r <- accountGroupsDAO.findByAccountId(accountId, sessionId)
    } yield (r)).flatMap(_ match {
      case Some(g) =>
        Future.value(g)
      case None =>
        for {
          id <- groupsDAO.create(sessionId)
          _ <- accountGroupsDAO.create(accountId, id, sessionId)
          _ <- accountGroupsDAO.create(sessionId.toAccountId, id, accountId.toSessionId)
          g <- accountGroupsDAO.validateFindByGroupId(id, sessionId)
        } yield (g)
    })
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountGroupsDAO.findHidden(groupId, sessionId).flatMap(_ match {
      case Some(true) =>
        accountGroupsDAO.updateHidden(groupId, false, sessionId)
      case Some(false) =>
        Future.exception(CactaceaException(GroupNotHidden))
      case None =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountGroupsDAO.findHidden(groupId, sessionId).flatMap(_ match {
      case Some(true) =>
        Future.exception(CactaceaException(GroupAlreadyHidden))
      case Some(false) =>
        accountGroupsDAO.updateHidden(groupId, true, sessionId)
      case None =>
        Future.exception(CactaceaException(AccountNotJoined))
    })
  }

}
