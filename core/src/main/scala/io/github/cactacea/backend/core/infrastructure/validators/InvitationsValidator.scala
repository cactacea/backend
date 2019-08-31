package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.InvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyInvited, AuthorityNotFound, InvitationNotFound}

@Singleton
class InvitationsValidator @Inject()(invitationsDAO: InvitationsDAO) {

  def mustNotInvited(accountId: AccountId, groupId: GroupId): Future[Unit] = {
    invitationsDAO.exists(accountId, groupId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def mustHasAuthority(accountId: AccountId, groupId: GroupId, sessionId: SessionId): Future[Unit] = {
    invitationsDAO.own(accountId, groupId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AuthorityNotFound))
    })
  }

  def mustFind(accountId: AccountId, invitationId: InvitationId): Future[(GroupId, AccountId)] = {
    invitationsDAO.find(accountId, invitationId).flatMap(_ match {
      case Some(gi) =>
        Future.value(gi)
      case None =>
        Future.exception(CactaceaException(InvitationNotFound))
    })
  }


}
