package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

@Singleton
class GroupAccountsRepository @Inject()(
                                         groupsDAO: GroupsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         groupAccountsDAO: GroupAccountsDAO,
                                         groupInvitationsDAO: GroupInvitationsDAO,
                                         messagesDAO: MessagesDAO,
                                         groupReportsDAO: GroupReportsDAO,
                                         validationDAO: ValidationDAO
                                       ) {

  def findAll(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      g <- validationDAO.findGroup(groupId)
      _ <- validationDAO.hasJoinAuthority(g, sessionId)
      r <- groupAccountsDAO.findAll(groupId, since, offset, count)
    } yield (r)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- validationDAO.notExistGroupAccount(accountId, groupId)
      g <- validationDAO.findNotInvitationOnlyGroup(groupId)
      _ <- validationDAO.hasJoinAuthority(g, sessionId)
      _ <- validationDAO.checkGroupAccountsCount(groupId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupInvitationsDAO.update(accountId, groupId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, g.accountCount, MessageType.joined, sessionId)
    } yield (Future.value(Unit))
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- validationDAO.notExistGroupAccount(accountId, groupId)
      g <- validationDAO.findNotInvitationOnlyGroup(groupId)
      _ <- validationDAO.hasJoinAndManagingAuthority(g, accountId, sessionId)
      _ <- validationDAO.checkGroupAccountsCount(groupId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupInvitationsDAO.update(accountId, groupId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, g.accountCount, MessageType.joined, accountId.toSessionId)
    } yield (Future.value(Unit))
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    (for {
      g <- validationDAO.findGroup(groupId)
      _ <- validationDAO.existGroupAccount(accountId, groupId)
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
          _ <- messagesDAO.create(groupId, g.accountCount, MessageType.left, accountId.toSessionId).map(_ => true)
        } yield (Future.value(Unit)))
      }
    )
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    (for {
      g <- validationDAO.findGroup(groupId)
      _ <- validationDAO.existAccount(accountId, sessionId)
      _ <- validationDAO.existAccount(sessionId.toAccountId, accountId.toSessionId)
      _ <- validationDAO.existGroupAccount(accountId, groupId)
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
          _ <- messagesDAO.create(groupId, g.accountCount, MessageType.left, accountId.toSessionId).map(_ => true)
        } yield (Future.value(Unit)))
      }
    )
  }

}
