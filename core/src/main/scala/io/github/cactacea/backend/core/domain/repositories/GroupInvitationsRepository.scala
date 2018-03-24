package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class GroupInvitationsRepository {

  @Inject private var accountsDAO: AccountsDAO = _
  @Inject private var groupsDAO: GroupsDAO = _
  @Inject private var groupAccountsDAO: GroupAccountsDAO = _
  @Inject private var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject private var accountGroupsDAO: AccountGroupsDAO = _
  @Inject private var messagesDAO: MessagesDAO = _
  @Inject private var validationDAO: ValidationDAO = _

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      p <- validationDAO.findGroup(groupId)
      _ <- validationDAO.notExistGroupAccount(accountId, groupId)
      _ <- validationDAO.notExistGroupInvitation(accountId, groupId)
      _ <- validationDAO.hasJoinAndManagingAuthority(p, accountId, sessionId)
      _ <- validationDAO.checkGroupAccountsCount(groupId)
      id <- groupInvitationsDAO.create(accountId, groupId, sessionId)
    } yield (id)
  }

  def findAll(since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsDAO.findAll(since, offset, count, sessionId)
      .map(_.map( t => GroupInvitation(t._1, t._2, t._3, t._4)))
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    (for {
      i <- validationDAO.findGroupsInvitation(invitationId, sessionId)
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
          _ <- messagesDAO.create(i.groupId, g.accountCount, i.accountId, MessageType.invitation, i.accountId.toSessionId)
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
