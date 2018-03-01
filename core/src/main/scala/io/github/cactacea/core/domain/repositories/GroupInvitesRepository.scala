package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{GroupInviteStatusType, MessageType}
import io.github.cactacea.core.domain.models.GroupInvite
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, GroupInviteId, SessionId}
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError

@Singleton
class GroupInvitesRepository {

  import CactaceaError._

  @Inject var accountsDAO: AccountsDAO = _
  @Inject var groupsDAO: GroupsDAO = _
  @Inject var groupAccountsDAO: GroupAccountsDAO = _
  @Inject var groupInvitesDAO: GroupInvitesDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var messagesDAO: MessagesDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInviteId]] = {
    Future.traverseSequentially(accountIds) {
      id => create(id, groupId, sessionId)
    }.map(_.toList)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInviteId] = {
    for {
      _ <- validationDAO.existAccounts(accountId, sessionId)
      g <- validationDAO.findGroups(groupId)
      _ <- validationDAO.notExistGroupAccounts(accountId, groupId)
      _ <- validationDAO.notExistGroupInvites(accountId, groupId)
      _ <- validationDAO.hasJoinAndManagingAuthority(g, accountId, sessionId)
      r <- groupInvitesDAO.create(accountId, groupId, sessionId)
    } yield (r)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvite]] = {
    groupInvitesDAO.findAll(since, offset, count, sessionId).map(_.map(t =>
      GroupInvite(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
    ))
  }

  def accept(inviteId: GroupInviteId, sessionId: SessionId): Future[Unit] = {
    (for {
      i <- validationDAO.findGroupsInvite(inviteId, sessionId)
      g <- validationDAO.findGroups(i.groupId)
      r <- groupAccountsDAO.exist(i.accountId, i.groupId)
    } yield ((r, g, i))).flatMap(_ match {
      case (true, _, i) =>
        groupInvitesDAO.update(i.accountId, i.groupId, GroupInviteStatusType.accepted).flatMap(_ => Future.Unit)
      case (false, g, i) =>
        for {
          _ <- accountGroupsDAO.create(i.accountId, i.groupId)
          _ <- groupInvitesDAO.update(i.accountId, i.groupId, GroupInviteStatusType.accepted)
          _ <- groupsDAO.updateAccountCount(i.groupId, 1L)
          _ <- messagesDAO.create(i.groupId, g.accountCount, i.accountId, MessageType.groupInvited, i.accountId.toSessionId)
        } yield (Future.value(Unit))
    })
  }

  def reject(inviteId: GroupInviteId, sessionId: SessionId): Future[Unit] = {
    groupInvitesDAO.exist(inviteId).flatMap(_ match {
      case true =>
        groupInvitesDAO.update(inviteId, GroupInviteStatusType.rejected, sessionId).flatMap(_ => Future.Unit)
      case false =>
        Future.exception(CactaceaException(GroupInviteNotFound))
    })
  }

}
