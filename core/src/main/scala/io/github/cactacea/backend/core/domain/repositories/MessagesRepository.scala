package io.github.cactacea.backend.core.domain.repositories

import com.google.inject.Inject
import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.Message
import io.github.cactacea.backend.core.infrastructure.dao._
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.validators._


class MessagesRepository @Inject()(
                                    accountChannelsDAO: AccountChannelsDAO,
                                    accountChannelsValidator: AccountChannelsValidator,
                                    accountMessagesDAO: AccountMessagesDAO,
                                    accountMessagesValidator: AccountMessagesValidator,
                                    channelsDAO: ChannelsDAO,
                                    mediumsValidator: MediumsValidator,
                                    messagesDAO:  MessagesDAO
                                  ) {

  def createText(channelId: ChannelId, message: String, sessionId: SessionId): Future[Message] = {
    for {
      _ <- accountChannelsValidator.mustJoined(sessionId.toAccountId, channelId)
      c <- channelsDAO.findAccountCount(channelId)
      i <- messagesDAO.create(channelId, message, c, sessionId)
      _ <- accountMessagesDAO.create(channelId, i, sessionId)
      _ <- accountChannelsDAO.updateUnreadCount(channelId)
      m <- accountMessagesValidator.mustFind(i, sessionId)
    } yield (m)
  }

  def createMedium(channelId: ChannelId, mediumId: MediumId, sessionId: SessionId): Future[Message] = {
    for {
      _ <- accountChannelsValidator.mustJoined(sessionId.toAccountId, channelId)
      _ <- mediumsValidator.mustOwn(mediumId, sessionId)
      c <- channelsDAO.findAccountCount(channelId)
      i <- messagesDAO.create(channelId, mediumId, c, sessionId)
      _ <- accountMessagesDAO.create(channelId, i, sessionId)
      _ <- accountChannelsDAO.updateUnreadCount(channelId)
      m <- accountMessagesValidator.mustFind(i, sessionId)
    } yield (m)
  }

  private def updateReadStatus(messages: List[Message], sessionId: SessionId): Future[Unit] = {
    val m = messages.filter(_.unread)
    if (m.size == 0) {
      Future.Unit
    } else {
      val ids = m.map(_.id)
      for {
        _ <- messagesDAO.updateReadCount(ids)
        _ <- accountMessagesDAO.updateUnread(ids, sessionId)
      } yield (())
    }
  }

  def delete(channelId: ChannelId, sessionId: SessionId): Future[Unit] = {
    accountMessagesDAO.delete(sessionId.toAccountId, channelId)
  }

  def find(channelId: ChannelId,
           since: Option[Long],
           offset: Int,
           count: Int,
           ascending: Boolean,
           sessionId: SessionId): Future[List[Message]] = {
    for {
      _ <- accountChannelsValidator.mustJoined(sessionId.toAccountId, channelId)
      r <- accountMessagesDAO.find(channelId, since, offset, count, ascending, sessionId)
      _ <- updateReadStatus(r, sessionId)
    } yield (r)

  }

}
