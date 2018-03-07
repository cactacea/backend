package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{GroupInvitationStatusType, MessageType, NotificationType}
import io.github.cactacea.core.domain.models.GroupInvitation
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._

@Singleton
class GroupInvitationsRepository {

  @Inject private var accountsDAO: AccountsDAO = _
  @Inject private var groupsDAO: GroupsDAO = _
  @Inject private var groupAccountsDAO: GroupAccountsDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var accountGroupsDAO: AccountGroupsDAO = _
  @Inject private var messagesDAO: MessagesDAO = _
  @Inject private var notificationsDAO: NotificationsDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(accountIds: List[AccountId], groupId: GroupId, sessionId: SessionId): Future[List[GroupInvitationId]] = {
    Future.traverseSequentially(accountIds) {
      id => create(id, groupId, sessionId)
    }.map(_.toList)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      p <- validationDAO.findGroup(groupId)
      _ <- validationDAO.notExistGroupAccount(accountId, groupId)
      _ <- validationDAO.notExistGroupInvite(accountId, groupId)
      _ <- validationDAO.hasJoinAndManagingAuthority(p, accountId, sessionId)
      _ <- validationDAO.checkGroupAccountsCount(groupId)
      id <- groupInvitationsDAO.create(accountId, groupId, sessionId)
      _ <- notificationsDAO.create(accountId, NotificationType.groupInvitation, id.value)
    } yield (id)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsDAO.findAll(since, offset, count, sessionId).map(_.map({ case (gi, a1, r1, g, m, um, a2, r2) =>
      GroupInvitation(gi, a1, r1, g, m, um, a2, r2)
    }))
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    (for {
      i <- validationDAO.findGroupsInvite(invitationId, sessionId)
      g <- validationDAO.findGroup(i.groupId)
      r <- groupAccountsDAO.exist(i.accountId, i.groupId)
      _ <- validationDAO.checkGroupAccountsCount(i.groupId)
    } yield ((r, g, i))).flatMap(_ match {
      case (true, _, i) =>
        groupInvitationsDAO.update(i.accountId, i.groupId, GroupInvitationStatusType.accepted).flatMap(_ => Future.Unit)
      case (false, g, i) =>
        for {
          _ <- accountGroupsDAO.create(i.accountId, i.groupId)
          _ <- groupInvitationsDAO.update(i.accountId, i.groupId, GroupInvitationStatusType.accepted)
          _ <- groupsDAO.updateAccountCount(i.groupId, 1L)
          _ <- messagesDAO.create(i.groupId, g.accountCount, i.accountId, MessageType.groupInvitationd, i.accountId.toSessionId)
        } yield (Future.value(Unit))
    })
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    groupInvitationsDAO.exist(invitationId).flatMap(_ match {
      case true =>
        groupInvitationsDAO.update(invitationId, GroupInvitationStatusType.rejected, sessionId).flatMap(_ => Future.Unit)
      case false =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
    })
  }

}
