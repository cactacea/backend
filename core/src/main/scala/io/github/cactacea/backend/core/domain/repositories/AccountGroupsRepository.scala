package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, GroupsDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountsValidator}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotJoined, GroupAlreadyHidden, GroupNotHidden}


class AccountGroupsRepository @Inject()(
                                         accountsValidator: AccountsValidator,
                                         accountGroupsValidator: AccountGroupsValidator,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         groupsDAO: GroupsDAO
                                       ) {

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
    } yield (())
  }

  def find(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Group]] = {
    for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      r <- accountGroupsDAO.find(accountId, since, offset, count, false, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    val accountId = sessionId.toAccountId
    accountGroupsDAO.find(accountId, since, offset, count, hidden, sessionId)
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    (for {
      _ <- accountsValidator.checkSessionId(accountId, sessionId)
      r <- accountGroupsDAO.findByAccountId(accountId, sessionId)
    } yield (r)).flatMap(_ match {
      case Some(g) =>
        Future.value(g)
      case None =>
        for {
          id <- groupsDAO.create(sessionId)
          _ <- accountGroupsDAO.create(accountId, id, sessionId)
          _ <- accountGroupsDAO.create(sessionId.toAccountId, id, accountId.toSessionId)
          g <- accountGroupsValidator.findByGroupId(id, sessionId)
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
