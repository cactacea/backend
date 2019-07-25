package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.GroupInvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyInvited, GroupInvitationNotFound}

@Singleton
class GroupInvitationsValidator @Inject()(groupInvitationsDAO: GroupInvitationsDAO) {

  def exist(invitationId: GroupInvitationId): Future[Unit] = {
    groupInvitationsDAO.exist(invitationId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
    })
  }

  def notExist(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    groupInvitationsDAO.findExist(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def find(groupInvitationId: GroupInvitationId, sessionId: SessionId): Future[(GroupId, AccountId)] = {
    groupInvitationsDAO.find(groupInvitationId, sessionId).flatMap(_ match {
      case Some(gi) =>
        Future.value(gi)
      case None =>
        Future.exception(CactaceaException(GroupInvitationNotFound))
    })
  }


}
