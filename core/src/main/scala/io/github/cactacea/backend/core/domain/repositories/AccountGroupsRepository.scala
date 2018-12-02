package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Group
import io.github.cactacea.backend.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, GroupsDAO, ValidationDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{GroupAlreadyHidden, GroupNotHidden}

@Singleton
class AccountGroupsRepository @Inject()(
                                         groupsDAO: GroupsDAO,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         validationDAO: ValidationDAO
                                       ) {

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
    } yield (Future.value(Unit))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Group]] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      r <- accountGroupsDAO.findAll(accountId, since, offset, count, false)
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    val accountId = sessionId.toAccountId
    accountGroupsDAO.findAll(accountId, since, offset, count, hidden)
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    accountGroupsDAO.findByAccountId(accountId, sessionId).flatMap(_ match {
      case Some((_, g)) =>
        Future.value(Group(g))
      case None =>
        (for {
          id <- groupsDAO.create(sessionId)
          _ <- accountGroupsDAO.create(accountId, id, sessionId)
          _ <- accountGroupsDAO.create(sessionId.toAccountId, id, accountId.toSessionId)
          (_, g) <- validationDAO.findAccountGroup(id, sessionId)
        } yield (g)).map(Group(_))
    })
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    validationDAO.findAccountGroup(groupId, sessionId).flatMap({ case (ag, _) =>
      if (ag.hidden) {
        accountGroupsDAO.updateHidden(groupId, false, sessionId)
      } else {
        Future.exception(CactaceaException(GroupNotHidden))
      }
    })
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    validationDAO.findAccountGroup(groupId, sessionId).flatMap({ case (ag, _) =>
      if (ag.hidden) {
        Future.exception(CactaceaException(GroupAlreadyHidden))
      } else {
        accountGroupsDAO.updateHidden(groupId, true, sessionId)
      }
    })
  }

}
