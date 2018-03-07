package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, GroupsDAO, ValidationDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

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
    accountGroupsDAO.findAll(accountId, since, offset, count, false, sessionId)
      .map(l => l.map({ case (g, m, am, a, r) => Group(g, m, am, a, r)}))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsDAO.findAll(sessionId.toAccountId, since, offset, count, hidden, sessionId)
      .map(l => l.map({ case (g, m, am, a, r) => Group(g, m, am, a, r)}))
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    accountGroupsDAO.find(accountId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(Group(t))
      case None =>
        (for {
          id <- groupsDAO.create(sessionId)
          _ <- accountGroupsDAO.create(accountId, id, sessionId)
          _ <- accountGroupsDAO.create(sessionId.toAccountId, id, accountId.toSessionId)
          g <- accountGroupsDAO.find(accountId, sessionId)
        } yield (g.head)).flatMap(g =>
          Future.value(Group(g))
        )
    })
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existsAccountGroup(groupId, sessionId)
      _ <- accountGroupsDAO.updateHidden(groupId, false, sessionId)
    } yield (Future.value(Unit))
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existsAccountGroup(groupId, sessionId)
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
    } yield (Future.value(Unit))
  }

}
