package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MessageType
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
                                         invitationsDAO: InvitationsDAO,
                                         groupReportsDAO: GroupReportsDAO,
                                         messagesDAO: MessagesDAO
                                       ) {

  def create(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- accountGroupsValidator.mustNotJoined(accountId, groupId)
      _ <- groupAuthorityValidator.hasJoinAuthority(groupId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId, accountId.toSessionId)
      c <- groupsDAO.findAccountCount(groupId)
      m <- messagesDAO.create(groupId, MessageType.joined, c, sessionId)
      _ <- accountMessagesDAO.create(groupId, m, sessionId)
    } yield (())
  }

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- accountGroupsValidator.mustNotJoined(accountId, groupId)
      _ <- groupAuthorityValidator.hasAddMembersAuthority(accountId, groupId, sessionId)
      _ <- accountGroupsDAO.create(accountId, groupId, accountId.toSessionId)
      c <- groupsDAO.findAccountCount(groupId)
      m <- messagesDAO.create(groupId, MessageType.joined, c, accountId.toSessionId)
      _ <- accountMessagesDAO.create(groupId, m, accountId.toSessionId)
    } yield (())
  }

  def delete(groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    for {
      _ <- groupsValidator.mustExist(groupId, sessionId)
      _ <- accountGroupsValidator.mustJoined(accountId, groupId)
      _ <- groupsValidator.mustNotLastOrganizer(groupId, sessionId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      c <- groupsDAO.findAccountCount(groupId)
      _ <- deleteGroup(accountId, groupId, c)
    } yield (())
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- groupsValidator.mustExist(groupId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- accountGroupsValidator.mustJoined(accountId, groupId)
      _ <- groupsValidator.mustNotLastOrganizer(groupId, accountId.toSessionId)
      _ <- groupAuthorityValidator.hasRemoveMembersAuthority(groupId, sessionId)
      _ <- accountGroupsDAO.delete(accountId, groupId)
      _ <- accountMessagesDAO.delete(accountId, groupId)
      c <- groupsDAO.findAccountCount(groupId)
      _ <- deleteGroup(accountId, groupId, c)
    } yield (())
  }

  private def deleteGroup(accountId: AccountId, groupId: GroupId, accountCount: Long): Future[Unit] = {
    if (accountCount == 0) {
      groupsDAO.delete(groupId)
    } else {
      (for {
        m <- messagesDAO.create(groupId, MessageType.left, accountCount, accountId.toSessionId)
        _ <- accountMessagesDAO.create(groupId, m, accountId.toSessionId)
      } yield (()))
    }
  }

  def find(groupId: GroupId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Account]] = {
    for {
      _ <- groupAuthorityValidator.hasFindMembersAuthority(groupId, sessionId)
      r <- groupAccountsDAO.find(groupId, since, offset, count)
    } yield (r)
  }

}
