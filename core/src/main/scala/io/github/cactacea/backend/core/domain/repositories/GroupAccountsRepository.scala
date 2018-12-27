package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}

@Singleton
class GroupAccountsRepository @Inject()(
                                         accountsDAO: AccountsDAO,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         groupsDAO: GroupsDAO,
                                         groupAccountsDAO: GroupAccountsDAO,
                                         groupAuthorityDAO: GroupAuthorityDAO,
                                         groupReportsDAO: GroupReportsDAO,
                                         groupInvitationsDAO: GroupInvitationsDAO,
                                         messagesDAO: MessagesDAO
                                       ) {

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- groupAuthorityDAO.validateSearchMembersAuthority(groupId, sessionId)
      r <- groupAccountsDAO.find(groupId, since, offset, count)
    } yield (r)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsDAO.validateNotExist(accountId, groupId)
      _ <- groupAuthorityDAO.validateJoinAuthority(groupId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupInvitationsDAO.update(groupId, accountId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, MessageType.groupJoined, sessionId)
    } yield (Unit)
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountGroupsDAO.validateNotExist(accountId, groupId)
      _ <- groupAuthorityDAO.validateAddMembersAuthority(accountId, groupId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupInvitationsDAO.update(groupId, accountId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, MessageType.groupJoined, accountId.toSessionId)
    } yield (Unit)
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    (for {
      _ <- groupsDAO.validateExist(groupId, sessionId)
      _ <- accountGroupsDAO.validateExist(accountId, groupId)
      c <- accountGroupsDAO.findAccountCount(groupId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
    } yield (c)).flatMap(c =>
      if (c == 0) {
        (for {
          _ <- messagesDAO.delete(groupId)
          _ <- groupReportsDAO.delete(groupId)
          _ <- groupInvitationsDAO.deleteByGroupId(groupId)
          _ <- groupsDAO.delete(groupId)
        } yield (Unit))
      } else {
        (for {
          _ <- messagesDAO.create(groupId, MessageType.groupLeft, accountId.toSessionId).map(_ => true)
        } yield (Unit))
      }
    )
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    (for {
      _ <- groupsDAO.validateExist(groupId, sessionId)
      _ <- accountsDAO.validateExist(accountId, sessionId)
      _ <- accountsDAO.validateExist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountGroupsDAO.validateExist(accountId, groupId)
      c <- accountGroupsDAO.findAccountCount(groupId)
      _ <- groupAuthorityDAO.validateRemoveMembersAuthority(groupId, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
    } yield (c)).flatMap(c =>
      if (c == 0) {
        (for {
          _ <- messagesDAO.delete(groupId)
          _ <- groupReportsDAO.delete(groupId)
          _ <- groupInvitationsDAO.deleteByGroupId(groupId)
          _ <- groupsDAO.delete(groupId)
        } yield (Unit))
      } else {
        (for {
          _ <- messagesDAO.create(groupId, MessageType.groupLeft, accountId.toSessionId).map(_ => true)
        } yield (Unit))
      }
    )
  }

}
