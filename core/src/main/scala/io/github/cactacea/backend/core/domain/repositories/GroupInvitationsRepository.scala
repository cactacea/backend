package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountsValidator, GroupAuthorityValidator, GroupInvitationsValidator}


class GroupInvitationsRepository @Inject()(
                                            accountGroupsDAO: AccountGroupsDAO,
                                            accountGroupsValidator: AccountGroupsValidator,
                                            accountsValidator: AccountsValidator,
                                            groupAuthorityValidator: GroupAuthorityValidator,
                                            groupInvitationsDAO: GroupInvitationsDAO,
                                            groupInvitationsValidator: GroupInvitationsValidator,
                                            notificationsDAO: NotificationsDAO,
                                            messagesDAO: MessagesDAO
                                          ) {

  def create(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[GroupInvitationId] = {
    for {
      _ <- accountsValidator.exist(accountId, sessionId)
      _ <- accountsValidator.exist(sessionId.toAccountId, accountId.toSessionId)
      _ <- accountGroupsValidator.notExist(accountId, groupId)
      _ <- groupInvitationsValidator.notExist(accountId, groupId)
      _ <- groupAuthorityValidator.hasInviteMembersAuthority(accountId, groupId, sessionId)
      i <- groupInvitationsDAO.create(accountId, groupId, sessionId)
      _ <- notificationsDAO.createGroupInvitation(i, accountId, sessionId)
    } yield (i)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsDAO.find(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    (for {
      (g, i) <- groupInvitationsValidator.find(invitationId, sessionId)
      r <- accountGroupsDAO.exist(g, i)
      _ <- groupInvitationsDAO.update(g, i, GroupInvitationStatusType.accepted)
    } yield (g, i, r)).flatMap(_ match {
      case (groupId, accountId, false) =>
        (for {
          _ <- accountGroupsDAO.create(accountId, groupId)
          _ <- groupInvitationsDAO.update(groupId, accountId, GroupInvitationStatusType.accepted)
          _ <- messagesDAO.create(groupId, MessageType.groupInvitation, accountId.toSessionId)
        } yield (()))
      case _ =>
        Future.value(())
    })
  }

  def reject(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- groupInvitationsValidator.exist(invitationId)
      _ <- groupInvitationsDAO.update(invitationId, GroupInvitationStatusType.rejected, sessionId)
    } yield ()
  }

}
