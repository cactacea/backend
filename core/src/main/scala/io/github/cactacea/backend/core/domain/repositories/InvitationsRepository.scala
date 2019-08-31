package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.domain.models.Invitation
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountsValidator, GroupAuthorityValidator, GroupsValidator, InvitationsValidator}


class InvitationsRepository @Inject()(
                                       accountGroupsDAO: AccountGroupsDAO,
                                       accountGroupsValidator: AccountGroupsValidator,
                                       accountsValidator: AccountsValidator,
                                       accountMessagesDAO: AccountMessagesDAO,
                                       groupsDAO: GroupsDAO,
                                       groupsValidator: GroupsValidator,
                                       groupAuthorityValidator: GroupAuthorityValidator,
                                       invitationsDAO: InvitationsDAO,
                                       invitationsValidator: InvitationsValidator,
                                       notificationsDAO: NotificationsDAO,
                                       messagesDAO: MessagesDAO
                                          ) {

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[InvitationId] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- groupsValidator.mustExist(groupId, sessionId)
      _ <- accountGroupsValidator.mustNotJoined(accountId, groupId)
      _ <- accountGroupsValidator.mustJoined(sessionId.toAccountId, groupId)
      _ <- invitationsValidator.mustNotInvited(accountId, groupId)
      _ <- groupAuthorityValidator.hasInviteAuthority(accountId, groupId, sessionId)
      i <- invitationsDAO.create(accountId, groupId, sessionId)
      _ <- notificationsDAO.create(i, accountId, sessionId)
    } yield (i)
  }

  def delete(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- groupsValidator.mustExist(groupId, sessionId)
      _ <- invitationsValidator.mustHasAuthority(accountId, groupId, sessionId)
      _ <- invitationsDAO.delete(accountId, groupId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Invitation]] = {
    invitationsDAO.find(since, offset, count, sessionId)
  }

  def accept(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      (g, a) <- invitationsValidator.mustFind(sessionId.toAccountId, invitationId)
      _ <- accountGroupsValidator.mustNotJoined(a, g)
      _ <- accountGroupsDAO.create(a, g, a.toSessionId)
      c <- groupsDAO.findAccountCount(g)
      m <- messagesDAO.create(g, MessageType.joined, c, a.toSessionId)
      _ <- accountMessagesDAO.create(g, m, sessionId)
      _ <- invitationsDAO.delete(invitationId, sessionId)
    } yield (())
  }

  def reject(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- invitationsValidator.mustFind(sessionId.toAccountId, invitationId)
      _ <- invitationsDAO.delete(invitationId, sessionId)
    } yield (())
  }

}
