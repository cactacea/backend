package io.github.cactacea.backend.core.infrastructure.validators

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.dao.InvitationsDAO
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{UserAlreadyInvited, AuthorityNotFound, InvitationNotFound}

@Singleton
class InvitationsValidator @Inject()(invitationsDAO: InvitationsDAO) {

  def mustNotInvited(userId: UserId, channelId: ChannelId): Future[Unit] = {
    invitationsDAO.exists(userId, channelId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(UserAlreadyInvited))
      case false =>
        Future.Unit
    })
  }

  def mustHasAuthority(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    invitationsDAO.own(userId, channelId, sessionId).flatMap(_ match {
      case true =>
        Future.Unit
      case false =>
        Future.exception(CactaceaException(AuthorityNotFound))
    })
  }

  def mustFind(userId: UserId, invitationId: InvitationId): Future[(ChannelId, UserId)] = {
    invitationsDAO.find(userId, invitationId).flatMap(_ match {
      case Some(gi) =>
        Future.value(gi)
      case None =>
        Future.exception(CactaceaException(InvitationNotFound))
    })
  }


}
