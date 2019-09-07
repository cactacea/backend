package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.enums.ChannelAuthorityType
import io.github.cactacea.backend.core.domain.models.Channel
import io.github.cactacea.backend.core.infrastructure.dao.{ChannelsDAO, UserChannelsDAO, UserMessagesDAO}
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, SessionId, UserId}
import io.github.cactacea.backend.core.infrastructure.validators.{UserChannelsValidator, UsersValidator}

@Singleton
class UserChannelRepository @Inject()(
                                       usersValidator: UsersValidator,
                                       userChannelsValidator: UserChannelsValidator,
                                       userChannelsDAO: UserChannelsDAO,
                                       userMessagesDAO: UserMessagesDAO,
                                       channelsDAO: ChannelsDAO
                                       ) {

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- userChannelsValidator.mustJoined(sessionId.userId, channelId)
      _ <- userChannelsDAO.updateHidden(channelId, true, sessionId)
      _ <- userMessagesDAO.delete(sessionId.userId, channelId)
    } yield (())
  }

  def find(userId: UserId, since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[Seq[Channel]] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      r <- userChannelsDAO.find(userId, since, offset, count, false, sessionId)
    } yield (r)
  }

  def find(since: Option[Long], offset: Int, count: Int, hidden: Boolean, sessionId: SessionId): Future[Seq[Channel]] = {
    userChannelsDAO.find(sessionId.userId, since, offset, count, hidden, sessionId)
  }

  def findOrCreate(userId: UserId, sessionId: SessionId): Future[Channel] = {
    for {
      _ <- usersValidator.mustNotSame(userId, sessionId)
      _ <- usersValidator.mustExist(userId, sessionId)
      g <- userChannelsDAO.findByUserId(userId, sessionId)
      r <- createIfNeeded(g, userId, sessionId)
    } yield (r)
  }

  private def createIfNeeded(channel: Option[Channel], userId: UserId, sessionId: SessionId): Future[Channel] = {
    channel match {
      case None =>
        for {
          i <- channelsDAO.create(sessionId)
          _ <- userChannelsDAO.create(i, ChannelAuthorityType.organizer, sessionId)
          _ <- userChannelsDAO.create(userId, i, ChannelAuthorityType.organizer, sessionId)
          g <- userChannelsValidator.mustFindByUserId(userId, sessionId)
        } yield (g)
      case Some(g) =>
        Future.value(g)
    }
  }

  def show(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- userChannelsValidator.mustHidden(channelId, sessionId)
      _ <- userChannelsDAO.updateHidden(channelId, false, sessionId)
    } yield (())
  }

  def hide(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- userChannelsValidator.mustNotHidden(channelId, sessionId)
      _ <- userChannelsDAO.updateHidden(channelId, true, sessionId)
    } yield (())
  }

}
