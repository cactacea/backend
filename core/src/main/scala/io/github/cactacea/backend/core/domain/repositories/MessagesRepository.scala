package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators._

@Singleton
class MessagesRepository @Inject()(
                                    userChannelsDAO: UserChannelsDAO,
                                    userChannelsValidator: UserChannelsValidator,
                                    userMessagesDAO: UserMessagesDAO,
                                    userMessagesValidator: UserMessagesValidator,
                                    channelsDAO: ChannelsDAO,
                                    mediumsValidator: MediumsValidator,
                                    messagesDAO:  MessagesDAO
                                  ) {

  def createText(channelId: ChannelId, message: String, sessionId: SessionId): Future[Message] = {
    for {
      _ <- userChannelsValidator.mustJoined(sessionId.userId, channelId)
      c <- channelsDAO.findUserCount(channelId)
      i <- messagesDAO.create(channelId, message, c, sessionId)
      _ <- userMessagesDAO.create(channelId, i, sessionId)
      _ <- userChannelsDAO.updateUnreadCount(channelId)
      m <- userMessagesValidator.mustFind(i, sessionId)
    } yield (m)
  }

  def createMedium(channelId: ChannelId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    for {
      _ <- userChannelsValidator.mustJoined(sessionId.userId, channelId)
      _ <- mediumsValidator.mustOwn(mediumId, sessionId)
      c <- channelsDAO.findUserCount(channelId)
      i <- messagesDAO.create(channelId, mediumId, c, sessionId)
      _ <- userMessagesDAO.create(channelId, i, sessionId)
      _ <- userChannelsDAO.updateUnreadCount(channelId)
      m <- userMessagesValidator.mustFind(i, sessionId)
    } yield (m)
  }

  private def updateReadStatus(messages: Seq[Message], sessionId: SessionId): Future[Unit] = {
    val m = messages.filter(_.unread)
    if (m.size == 0) {
      Future.Unit
    } else {
      val ids = m.map(_.id)
      for {
        _ <- messagesDAO.updateReadCount(ids)
        _ <- userMessagesDAO.updateUnread(ids, sessionId)
      } yield (())
    }
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    userMessagesDAO.delete(sessionId.userId, channelId)
  }

  def find(channelId: ChannelId,
           since: Option[Long],
           offset: Int,
           count: Int,
           ascending: Boolean,
           sessionId: SessionId): Future[Seq[Message]] = {
    for {
      _ <- userChannelsValidator.mustJoined(sessionId.userId, channelId)
      r <- userMessagesDAO.find(channelId, since, offset, count, ascending, sessionId)
      _ <- updateReadStatus(r, sessionId)
    } yield (r)

  }

}
