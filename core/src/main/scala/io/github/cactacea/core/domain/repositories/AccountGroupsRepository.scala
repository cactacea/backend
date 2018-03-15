package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, GroupsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.{GroupAlreadyHidden, GroupNotHidden}

@Singleton
class AccountGroupsRepository {

  @Inject private var groupsDAO: GroupsDAO = _
  @Inject private var accountGroupsDAO: AccountGroupsDAO = _
  @Inject private var accountMessagesDAO: AccountMessagesDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
    } yield (Future.value(Unit))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Group]] = {
    for {
      _ <- validationDAO.existAccount(accountId)
      r <- accountGroupsDAO.findAll(accountId, since, offset, count, false)
        .map(l => l.map({ case (g, m, am, a, r, id) => Group(g, m, am, a, r, id)}))
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    val accountId = sessionId.toAccountId
    accountGroupsDAO.findAll(accountId, since, offset, count, hidden)
      .map(l => l.map({ case (g, m, am, a, r, id) => Group(g, m, am, a, r, id)}))
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
          t <- validationDAO.findAccountGroup(id, sessionId)
        } yield (t._2)).map(Group(_))
    })
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    validationDAO.findAccountGroup(groupId, sessionId).flatMap({ case (ag, g) =>
      if (ag.hidden) {
        accountGroupsDAO.updateHidden(groupId, false, sessionId).map(_ => Unit)
      } else {
        Future.exception(CactaceaException(GroupNotHidden))
      }
    })
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    validationDAO.findAccountGroup(groupId, sessionId).flatMap({ case (ag, g) =>
      if (ag.hidden) {
        Future.exception(CactaceaException(GroupAlreadyHidden))
      } else {
        accountGroupsDAO.updateHidden(groupId, true, sessionId).map(_ => Unit)
      }
    })
  }

}
