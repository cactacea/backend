package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, MessageType}
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{ChannelAuthorityValidator, ChannelsValidator, UserChannelsValidator, UsersValidator}


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
      _ <- channelAuthorityValidator.canJoin(channelId, sessionId)
      _ <- userChannelsDAO.create(userId, channelId, ChannelAuthorityType.member, userId.sessionId)
      c <- channelsDAO.findUserCount(channelId)
      m <- messagesDAO.create(channelId, MessageType.joined, c, sessionId)
      _ <- userMessagesDAO.create(channelId, m, sessionId)
    } yield (())
  }

  def create(userId: UserId, channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- usersValidator.mustExist(userId, sessionId)
      _ <- userChannelsValidator.mustNotJoined(userId, channelId)
      _ <- channelAuthorityValidator.canAddMember(userId, channelId, sessionId)
      _ <- userChannelsDAO.create(userId, channelId, ChannelAuthorityType.member, userId.sessionId)
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
      _ <- channelAuthorityValidator.canLeave(channelId, sessionId)
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
      _ <- channelAuthorityValidator.canLeaveMember(userId, channelId, sessionId)
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
      _ <- channelAuthorityValidator.canFindMembers(channelId, sessionId)
      r <- channelUsersDAO.find(channelId, since, offset, count)
    } yield (r)
  }

}
