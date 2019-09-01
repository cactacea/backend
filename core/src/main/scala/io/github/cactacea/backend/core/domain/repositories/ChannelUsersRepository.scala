package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.MessageType
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId, SessionId}
import io.github.cactacea.backend.core.infrastructure.validators.{UserChannelsValidator, UsersValidator, ChannelAuthorityValidator, ChannelsValidator}


class ChannelUsersRepository @Inject()(
                                           usersValidator: UsersValidator,
                                           userChannelsDAO: UserChannelsDAO,
                                           userChannelsValidator: UserChannelsValidator,
                                           userMessagesDAO: UserMessagesDAO,
                                           channelsValidator: ChannelsValidator,
                                           channelUsersDAO: ChannelUsersDAO,
                                           channelAuthorityValidator: ChannelAuthorityValidator,
                                           channelsDAO: ChannelsDAO,
                                           messagesDAO: MessagesDAO
                                       ) {

  def create(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    for {
      _ <- userChannelsValidator.mustNotJoined(userId, channelId)
      _ <- channelAuthorityValidator.hasJoinAuthority(channelId, sessionId)
      _ <- userChannelsDAO.create(userId, channelId, userId.sessionId)
      c <- channelsDAO.findUserCount(channelId)
      m <- messagesDAO.create(channelId, MessageType.joined, c, sessionId)
      _ <- userMessagesDAO.create(channelId, m, sessionId)
    } yield (())
  }

  def create(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- userChannelsValidator.mustNotJoined(userId, channelId)
      _ <- channelAuthorityValidator.hasAddMembersAuthority(userId, channelId, sessionId)
      _ <- userChannelsDAO.create(userId, channelId, userId.sessionId)
      c <- channelsDAO.findUserCount(channelId)
      m <- messagesDAO.create(channelId, MessageType.joined, c, userId.sessionId)
      _ <- userMessagesDAO.create(channelId, m, userId.sessionId)
    } yield (())
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    for {
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- userChannelsValidator.mustJoined(userId, channelId)
      _ <- channelsValidator.mustNotLastOrganizer(channelId, sessionId)
      _ <- userChannelsDAO.delete(userId, channelId)
      _ <- userMessagesDAO.delete(userId, channelId)
      c <- channelsDAO.findUserCount(channelId)
      _ <- deleteChannel(userId, channelId, c)
    } yield (())
  }

  def delete(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- channelsValidator.mustExist(channelId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- userChannelsValidator.mustJoined(userId, channelId)
      _ <- channelsValidator.mustNotLastOrganizer(channelId, userId.sessionId)
      _ <- channelAuthorityValidator.hasRemoveMembersAuthority(channelId, sessionId)
      _ <- userChannelsDAO.delete(userId, channelId)
      _ <- userMessagesDAO.delete(userId, channelId)
      c <- channelsDAO.findUserCount(channelId)
      _ <- deleteChannel(userId, channelId, c)
    } yield (())
  }

  private def deleteChannel(userId: UserId, channelId: ChannelId, userCount: Long): Future[Unit] = {
    if (userCount == 0) {
      channelsDAO.delete(channelId)
    } else {
      (for {
        m <- messagesDAO.create(channelId, MessageType.left, userCount, userId.sessionId)
        _ <- userMessagesDAO.create(channelId, m, userId.sessionId)
      } yield (()))
    }
  }

  def find(channelId: ChannelId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[User]] = {
    for {
      _ <- channelAuthorityValidator.hasFindMembersAuthority(channelId, sessionId)
      r <- channelUsersDAO.find(channelId, since, offset, count)
    } yield (r)
  }

}
