package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, MessageType}
import io.github.cactacea.backend.core.domain.models.Invitation
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators._

@Singleton
class InvitationsRepository @Inject()(
                                       userChannelsDAO: UserChannelsDAO,
                                       userChannelsValidator: UserChannelsValidator,
                                       usersValidator: UsersValidator,
                                       userMessagesDAO: UserMessagesDAO,
                                       channelsDAO: ChannelsDAO,
                                       channelsValidator: ChannelsValidator,
                                       channelAuthorityValidator: ChannelAuthorityValidator,
                                       invitationsDAO: InvitationsDAO,
                                       invitationsValidator: InvitationsValidator,
                                       notificationsDAO: NotificationsDAO,
                                       messagesDAO: MessagesDAO
                                          ) {

  def create(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[InvitationId] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- userChannelsValidator.mustNotJoined(userId, channelId)
      _ <- userChannelsValidator.mustJoined(sessionId.userId, channelId)
      _ <- invitationsValidator.mustNotInvited(userId, channelId)
      _ <- channelAuthorityValidator.canInvite(userId, channelId, sessionId)
      i <- invitationsDAO.create(userId, channelId, sessionId)
      _ <- notificationsDAO.create(i, userId, sessionId)
    } yield (i)
  }

  def delete(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- invitationsValidator.mustHasAuthority(userId, channelId, sessionId)
      _ <- invitationsDAO.delete(userId, channelId, sessionId)
    } yield (())
  }

  def find(since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Invitation]] = {
    invitationsDAO.find(since, offset, count, sessionId)
  }

  def accept(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      (g, a) <- invitationsValidator.mustFind(sessionId.userId, invitationId)
      _ <- userChannelsValidator.mustNotJoined(a, g)
      _ <- userChannelsDAO.create(a, g, ChannelAuthorityType.member, a.sessionId)
      c <- channelsDAO.findUserCount(g)
      m <- messagesDAO.create(g, MessageType.joined, c, a.sessionId)
      _ <- userMessagesDAO.create(g, m, sessionId)
      _ <- invitationsDAO.delete(invitationId, sessionId)
    } yield (())
  }

  def reject(invitationId: InvitationId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- invitationsValidator.mustFind(sessionId.userId, invitationId)
      _ <- invitationsDAO.delete(invitationId, sessionId)
    } yield (())
  }

}
