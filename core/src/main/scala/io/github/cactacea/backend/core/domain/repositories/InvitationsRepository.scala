package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.domain.models.Invitation
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators._

class InvitationsRepository @Inject()(
                                       accountChannelsDAO: AccountChannelsDAO,
                                       accountChannelsValidator: AccountChannelsValidator,
                                       accountsValidator: AccountsValidator,
                                       accountMessagesDAO: AccountMessagesDAO,
                                       channelsDAO: ChannelsDAO,
                                       channelsValidator: ChannelsValidator,
                                       channelAuthorityValidator: ChannelAuthorityValidator,
                                       invitationsDAO: InvitationsDAO,
                                       invitationsValidator: InvitationsValidator,
                                       notificationsDAO: NotificationsDAO,
                                       messagesDAO: MessagesDAO
                                          ) {

  def create(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[InvitationId] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- accountChannelsValidator.mustNotJoined(accountId, channelId)
      _ <- accountChannelsValidator.mustJoined(sessionId.toAccountId, channelId)
      _ <- invitationsValidator.mustNotInvited(accountId, channelId)
      _ <- channelAuthorityValidator.hasInviteAuthority(accountId, channelId, sessionId)
      i <- invitationsDAO.create(accountId, channelId, sessionId)
      _ <- notificationsDAO.create(i, accountId, sessionId)
    } yield (i)
  }

  def delete(accountId: AccountId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- accountsValidator.mustNotSame(accountId, sessionId)
      _ <- accountsValidator.mustExist(accountId, sessionId)
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- invitationsValidator.mustHasAuthority(accountId, channelId, sessionId)
      _ <- invitationsDAO.delete(accountId, channelId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[Invitation]] = {
    invitationsDAO.find(since, offset, count, sessionId)
  }

  def accept(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      (g, a) <- invitationsValidator.mustFind(sessionId.toAccountId, invitationId)
      _ <- accountChannelsValidator.mustNotJoined(a, g)
      _ <- accountChannelsDAO.create(a, g, a.toSessionId)
      c <- channelsDAO.findAccountCount(g)
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
