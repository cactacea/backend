package io.github.cactacea.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

class GroupAccountsRepository {

  @Inject var groupsDAO: GroupsDAO = _
  @Inject var accountMessagesDAO: AccountMessagesDAO = _
  @Inject var accountGroupsDAO: AccountGroupsDAO = _
  @Inject var groupAccountsDAO: GroupAccountsDAO = _
  @Inject var groupInvitationsDAO: GroupInvitationsDAO = _
  @Inject var messagesDAO: MessagesDAO = _
  @Inject var groupReportsDAO: GroupReportsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  def findAll(groupId: GroupId, since: Option[Long], offset: Option[Int], count: Option[Int], sessionId: SessionId): Future[List[Account]] = {
    for {
      g <- validationDAO.findGroups(groupId)
      _ <- validationDAO.hasJoinAuthority(g, sessionId)
      r <- groupAccountsDAO.findAll(groupId, since, offset, count, sessionId).map(_.map(t => Account(t._1, t._2, t._3)))
    } yield (r)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- validationDAO.notExistGroupAccounts(accountId, groupId)
      g <- validationDAO.findNotInvitationOnlyGroups(groupId)
      _ <- validationDAO.hasJoinAuthority(g, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupsDAO.updateAccountCount(groupId, 1L)
      _ <- groupInvitationsDAO.update(accountId, groupId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, g.accountCount, accountId, MessageType.groupJoined, sessionId)
    } yield (Future.value(Unit))
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.notExistGroupAccounts(accountId, groupId)
      g <- validationDAO.findNotInvitationOnlyGroups(groupId)
      _ <- validationDAO.hasJoinAndManagingAuthority(g, accountId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupsDAO.updateAccountCount(groupId, 1L)
      _ <- groupInvitationsDAO.update(accountId, groupId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, g.accountCount, accountId, MessageType.groupJoined, sessionId)
    } yield (Future.value(Unit))
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    (for {
      g <- validationDAO.findGroups(groupId)
      _ <- validationDAO.existGroupAccounts(accountId, groupId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
    } yield (g)).flatMap(g =>
      if (g.accountCount == 1) {
        (for {
          _ <- messagesDAO.delete(groupId)
          _ <- groupReportsDAO.delete(groupId)
          _ <- groupInvitationsDAO.deleteByGroupId(groupId)
          _ <- groupsDAO.delete(groupId)
        } yield (Future.value(Unit)))
      } else {
        (for {
          _ <- groupsDAO.updateAccountCount(groupId, -1L)
          _ <- messagesDAO.create(groupId, g.accountCount, accountId, MessageType.groupLeft, accountId.toSessionId).map(_ => true)
        } yield (Future.value(Unit)))
      }
    )
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    (for {
      g <- validationDAO.findGroups(groupId)
      _ <- validationDAO.existAccounts(accountId, sessionId)
      _ <- validationDAO.existGroupAccounts(accountId, groupId)
      _ <- validationDAO.hasJoinAndManagingAuthority(g, accountId, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
    } yield (g)).flatMap(g =>
      if (g.accountCount == 1) {
        (for {
          _ <- messagesDAO.delete(groupId)
          _ <- groupReportsDAO.delete(groupId)
          _ <- groupInvitationsDAO.deleteByGroupId(groupId)
          _ <- groupsDAO.delete(groupId)
        } yield (Future.value(Unit)))
      } else {
        (for {
          _ <- groupsDAO.updateAccountCount(groupId, -1L)
          _ <- messagesDAO.create(groupId, g.accountCount, accountId, MessageType.groupLeft, accountId.toSessionId).map(_ => true)
        } yield (Future.value(Unit)))
      }
    )
  }

}
