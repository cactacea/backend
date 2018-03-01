package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.infrastructure.dao.{AccountGroupsDAO, AccountMessagesDAO, GroupsDAO}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError.GroupNotFound

@Singleton
class AccountGroupsRepository {

  @Inject var groupsDAO: GroupsDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var accountMessagesDAO: AccountMessagesDAO = _

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsDAO.updateHidden(groupId, true, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
    } yield (Future.value(Unit))
  }

  def findAll(accountId: AccountId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Group]] = {
    accountGroupsDAO.findAll(accountId, since, offset, count, false, sessionId)
      .map(l => l.map(t => Group(t._1, t._2, t._3, t._4, t._5)))
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], hidden: Boolean, sessionId: SessionId): Future[List[Group]] = {
    accountGroupsDAO.findAll(sessionId.toAccountId, since, offset, count, hidden, sessionId)
      .map(l => l.map(t => Group(t._1, t._2, t._3, t._4, t._5)))
  }

  def findOrCreate(accountId: AccountId, sessionId: SessionId): Future[Group] = {
    accountGroupsDAO.find(accountId, sessionId).flatMap(_ match {
      case Some(t) =>
        Future.value(Group(t))
      case None =>
        (for {
          groupId <- groupsDAO.create(sessionId)
          _ <- accountGroupsDAO.create(accountId, groupId, sessionId)
          _ <- accountGroupsDAO.create(sessionId.toAccountId, groupId, accountId.toSessionId)
          g <- accountGroupsDAO.find(accountId, sessionId)
        } yield (g.head)).flatMap(g =>
          Future.value(Group(g))
        )
    })
  }

  def show(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountGroupsDAO.updateHidden(groupId, false, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

  def hide(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    accountGroupsDAO.updateHidden(groupId, true, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(GroupNotFound))
    })
  }

}
