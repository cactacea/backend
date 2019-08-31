package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.InvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyInvited, AuthorityNotFound, InvitationNotFound}

@Singleton
class InvitationsValidator @Inject()(invitationsDAO: InvitationsDAO) {

  def mustNotInvited(accountId: AccountId, channelId: ChannelId): Future[Unit] = {
    invitationsDAO.exists(accountId, channelId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def mustHasAuthority(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    invitationsDAO.own(accountId, channelId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AuthorityNotFound))
    })
  }

  def mustFind(accountId: AccountId, invitationId: InvitationId): Future[(ChannelId, AccountId)] = {
    invitationsDAO.find(accountId, invitationId).flatMap(_ match {
      case Some(gi) =>
        Future.value(gi)
      case None =>
        Future.exception(CactaceaException(InvitationNotFound))
    })
  }


}
