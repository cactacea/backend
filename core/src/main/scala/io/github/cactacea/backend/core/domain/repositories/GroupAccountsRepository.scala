package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.backend.core.domain.models.Account
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountsValidator, GroupAuthorityValidator, GroupsValidator}


class GroupAccountsRepository @Inject()(
                                         accountsValidator: AccountsValidator,
                                         accountGroupsDAO: AccountGroupsDAO,
                                         accountGroupsValidator: AccountGroupsValidator,
                                         accountMessagesDAO: AccountMessagesDAO,
                                         groupsValidator: GroupsValidator,
                                         groupAccountsDAO: GroupAccountsDAO,
                                         groupAuthorityValidator: GroupAuthorityValidator,
                                         groupsDAO: GroupsDAO,
                                         groupInvitationsDAO: GroupInvitationsDAO,
                                         groupReportsDAO: GroupReportsDAO,
                                         messagesDAO: MessagesDAO
                                       ) {

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- groupAuthorityValidator.hasFindMembersAuthority(groupId, sessionId)
      r <- groupAccountsDAO.find(groupId, since, offset, count)
    } yield (r)
  }

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsValidator.notExist(accountId, groupId)
      _ <- groupAuthorityValidator.hasJoinAuthority(groupId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupInvitationsDAO.update(groupId, accountId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, MessageType.groupJoined, sessionId)
    } yield (())
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountGroupsValidator.notExist(accountId, groupId)
      _ <- groupAuthorityValidator.hasAddMembersAuthority(accountId, groupId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId)
      _ <- groupInvitationsDAO.update(groupId, accountId, GroupInvitationStatusType.accepted)
      _ <- messagesDAO.create(groupId, MessageType.groupJoined, accountId.toSessionId)
    } yield (())
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    (for {
      _ <- groupsValidator.exist(groupId, sessionId)
      _ <- accountGroupsValidator.exist(accountId, groupId)
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
        } yield (()))
      } else {
        (for {
          _ <- messagesDAO.create(groupId, MessageType.groupLeft, accountId.toSessionId).map(_ => true)
        } yield (()))
      }
    )
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    (for {
      _ <- groupsValidator.exist(groupId, sessionId)
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountGroupsValidator.exist(accountId, groupId)
      _ <- groupAuthorityValidator.hasRemoveMembersAuthority(groupId, sessionId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
      c <- accountGroupsDAO.findAccountCount(groupId)
    } yield (c)).flatMap(c =>
      if (c == 0) {
        (for {
          _ <- messagesDAO.delete(groupId)
          _ <- groupReportsDAO.delete(groupId)
          _ <- groupInvitationsDAO.deleteByGroupId(groupId)
          _ <- groupsDAO.delete(groupId)
        } yield (()))
      } else {
        (for {
          _ <- messagesDAO.create(groupId, MessageType.groupLeft, accountId.toSessionId).map(_ => true)
        } yield (()))
      }
    )
  }

}
