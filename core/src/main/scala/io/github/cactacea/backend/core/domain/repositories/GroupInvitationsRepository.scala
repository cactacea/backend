package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{GroupInvitationStatusType, MessageType}
import io.github.cactacea.backend.core.domain.models.GroupInvitation
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators.{AccountGroupsValidator, AccountsValidator, GroupAuthorityValidator, GroupInvitationsValidator}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors._

@Singleton
class GroupInvitationsRepository @Inject()(
                                            accountsValidator: AccountsValidator,
                                            accountGroupsValidator: AccountGroupsValidator,
                                            groupAuthorityValidator: GroupAuthorityValidator,
                                            groupInvitationsValidator: GroupInvitationsValidator,
                                            accountGroupsDAO: AccountGroupsDAO,
                                            groupInvitationsDAO: GroupInvitationsDAO,
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
      id <- groupInvitationsDAO.create(accountId, groupId, sessionId)
      _ <- notificationsDAO.createGroupInvitation(id, accountId, sessionId)
    } yield (id)
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[GroupInvitation]] = {
    groupInvitationsDAO.find(since, offset, count, sessionId)
  }

  def accept(invitationId: GroupInvitationId, sessionId: SessionId): Future[Unit] = {
    (for {
      (groupId, accountId) <- groupInvitationsValidator.find(invitationId, sessionId)
      r <- accountGroupsDAO.exist(groupId, accountId)
      _ <- groupInvitationsDAO.update(groupId, accountId, GroupInvitationStatusType.accepted)
    } yield (groupId, accountId, r)).flatMap(_ match {
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
    groupInvitationsDAO.exist(invitationId).flatMap(_ match {
      case true =>
        groupInvitationsDAO.update(invitationId, GroupInvitationStatusType.rejected, sessionId)
      case false =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
    })
  }

}
